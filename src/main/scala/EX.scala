package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util.MuxLookup

class Execution extends MultiIOModule {
  val io = IO(
    new Bundle {
      val in = Input(new IDEXBundle)
      val out = Output(new EXMEMBundle)

      val memIn = Input(new MEMEXBundle)
      val wbIn = Input(new WBEXBundle)
    }
  )

  val op1 = Wire(UInt(32.W))
  val op2 = Wire(UInt(32.W))

  when(io.memIn.RegWrite && io.memIn.RegDest === io.in.rd1) {
    op1 := io.memIn.RegVal
  }.elsewhen(io.wbIn.RegWrite && io.wbIn.RegDest === io.in.rd1 && io.memIn.RegDest =/= io.in.rd1) {
    op1 := io.wbIn.RegVal
  }.otherwise {
    op1 := MuxLookup(io.in.Op1Select, 0.U, Array(
      Op1Select.rs1 -> io.in.rd1,
      Op1Select.PC  -> (io.in.pc + 4.U),
      Op1Select.DC  -> 0.U,
    ))
  }

  when(io.memIn.RegWrite && io.memIn.RegDest === io.in.rd2) {
    op2 := io.memIn.RegVal
  }.elsewhen(io.wbIn.RegWrite && io.wbIn.RegDest === io.in.rd2 && io.memIn.RegDest =/= io.in.rd2) {
    op2 := io.wbIn.RegVal
  }.otherwise {
    op2 := MuxLookup(io.in.Op2Select, 0.U, Array(
      Op2Select.rs2 -> io.in.rd2,
      Op2Select.imm -> io.in.Imm,
      Op2Select.DC  -> 0.U,
    ))
  }

  val ALUopMap = Array(
    ALUOps.ADD  -> (op1 + op2),
    ALUOps.SUB  -> (op1 - op2),
    ALUOps.AND  -> (op1 & op2),
    ALUOps.OR   -> (op1 | op2),
    ALUOps.XOR  -> (op1 ^ op2),
    ALUOps.SLT  -> (op1.asSInt < op2.asSInt),
    ALUOps.SLTU -> (op1 <  op2),
    ALUOps.SRA  -> ((op1.asSInt >> op2(4, 0)).asUInt),
    ALUOps.SRL  -> (op1 >> op2(4, 0)),
    ALUOps.SLL  -> (op1 << op2(4, 0)),
    ALUOps.DC   -> (0.U)
  )

  val BranchMap = Array(
    branchType.beq  -> (op1 === op2),
    branchType.neq  -> (op1 =/= op2),
    branchType.gte  -> (op1.asSInt >= op2.asSInt),
    branchType.lt   -> (op1.asSInt <  op2.asSInt),
    branchType.gteu -> (op1 >= op2),
    branchType.ltu  -> (op1 <  op2),
    branchType.jal  -> (Bool(true)),
    branchType.jalr -> (Bool(true)),
    branchType.DC   -> (Bool(false)),
  )

  io.out.controlSignals := io.in.controlSignals
  io.out.BranchOut := MuxLookup(io.in.BranchType, Bool(false), BranchMap)
  io.out.ALUOut := Mux(io.in.controlSignals.jump, op1, MuxLookup(io.in.ALUop, 0.U(32.W), ALUopMap))

  io.out.rd2 := io.in.rd2
  io.out.RegDest := io.in.RegDest
  io.out.NewPC := Mux(io.in.BranchType === branchType.jalr, (op1 + io.in.Imm) & "hfffffffe".U, io.in.pc + io.in.Imm)
}
