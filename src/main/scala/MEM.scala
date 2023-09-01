package FiveStage
import chisel3._
import chisel3.util._
import chisel3.experimental.MultiIOModule


class MemoryFetch() extends MultiIOModule {


  // Don't touch the test harness
  val testHarness = IO(
    new Bundle {
      val DMEMsetup      = Input(new DMEMsetupSignals)
      val DMEMpeek       = Output(UInt(32.W))

      val testUpdates    = Output(new MemUpdates)
    })

  val io = IO(
    new Bundle {
      val in = Input(new EXMEMBundle)
      val out = Output(new MEMWBBundle)
      val outIF = Output(new MEMIFBundle)
    }
  )


  val DMEM = Module(new DMEM)


  /**
    * Setup. You should not change this code
    */
  DMEM.testHarness.setup  := testHarness.DMEMsetup
  testHarness.DMEMpeek    := DMEM.io.dataOut
  testHarness.testUpdates := DMEM.testHarness.testUpdates


  /**
    * Your code here.
    */
  DMEM.io.dataIn      := 0.U
  DMEM.io.dataAddress := 0.U
  DMEM.io.writeEnable := false.B

  io.out.RegWrite := io.in.RegWrite
  io.out.MemRead := io.in.MemRead
  io.out.ALUOut := io.in.ALUOut
  io.out.WriteReg := io.in.WriteReg

  io.outIF.PCBranch := RegInit(0.U(32.W))
  io.outIF.PCSrc := RegInit(0.U(32.W))
}
