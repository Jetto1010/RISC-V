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
  val op1 = io.in.op1
  val op2 = io.in.op2
  val ALUopMap = Array(
    ADD    -> (op1 + op2),
    SUB    -> (op1 - op2),
    AND    -> (op1 & op2),
    OR     -> (op1 | op2),
    XOR    -> (op1 ^ op2),
    // SLT    -> (),
    // SLL    -> (),
    // SLTU   -> (),
    // SRL    -> (),
    // SRA    -> (),
    // COPY_A -> (),
    // COPY_B -> (),
    // DC     -> (),
  )
  io.out.ALUOut := MuxLookup(io.in.opcode, 0.U(32.W), ALUopMap)

  io.out.RegWrite := io.in.RegWrite
  io.out.MemtoReg := io.in.MemtoReg
  io.out.MemWrite := io.in.MemWrite
  io.out.Branch := io.in.Branch
}
