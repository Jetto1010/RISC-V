package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util.MuxLookup

class Execution extends MultiIOModule {
  val io = IO(
    new Bundle {
      val in = Input(new IDEXBundle)
      val out = Output(new EXMEMBundle)
    }
  )

  val op1 = io.in.Op1Select
  val op2 = io.in.Op2Select
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
    branchType.lt   -> (op1.asSInt < op2.asSInt),
    branchType.gteu -> (op1 >= op2),
    branchType.ltu  -> (op1 <  op2),
    branchType.jal  -> (Bool(true)),
    branchType.jalr -> (Bool(true)),
    branchType.DC   -> (Bool(false)),
  )

  io.out.controlSignals := io.in.controlSignals
  io.out.BranchOut := MuxLookup(io.in.BranchType, 0.U(1.W), BranchMap)
  //io.out.ALUOut := Mux(io.in.controlSignals.jump, op1 + 4.U, MuxLookup(io.in.ALUop, 0.U(32.W), ALUopMap))
  io.out.ALUOut := MuxLookup(io.in.ALUop, 0.U(32.W), ALUopMap)

  io.out.rd2 := io.in.rd2
  io.out.RegDest := io.in.RegDest
  // io.out.NewPC := op1 + op2
  io.out.NewPC := Mux(io.in.BranchType === branchType.jalr, (io.in.pc + io.in.Imm) & "hfffffffe".U, io.in.pc + io.in.Imm)
}
