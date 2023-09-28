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

  val res = RegInit(UInt(32.W), 0.U)
  val op1 = io.in.Op1Select
  val op2 = io.in.Op2Select
  val ALUopMap = Array(
    ALUOps.ADD    -> (op1 + op2),
    ALUOps.SUB    -> (op1 - op2),
    ALUOps.AND    -> (op1 & op2),
    ALUOps.OR     -> (op1 | op2),
    ALUOps.XOR    -> (op1 ^ op2),
    ALUOps.SLT    -> (op1.asSInt() < op2.asSInt()),
    ALUOps.SLL    -> (op1 << op2(4, 0)),
    ALUOps.SLTU   -> (op1 <  op2),
    ALUOps.SRL    -> (op1 >> op2(4, 0)),
    ALUOps.SRA    -> ((op1.asSInt() >> op2(4, 0)).asUInt()),
    ALUOps.COPY_A -> (op1),
    ALUOps.COPY_B -> (op2),
    ALUOps.DC     -> (0.U),
  )
  io.out.controlSignals := io.in.controlSignals
  io.out.BranchType := io.in.BranchType
  io.out.ALUOut := MuxLookup(io.in.ALUop, 0.U(32.W), ALUopMap)
  io.out.rd2 := io.in.rd2
}
