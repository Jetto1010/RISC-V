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
  DMEM.io.dataIn      := io.in.rd2
  DMEM.io.dataAddress := io.in.ALUOut
  DMEM.io.writeEnable := io.in.controlSignals.memWrite

  io.out.controlSignals := io.in.controlSignals
  io.out.ALUOut := io.in.ALUOut
  io.out.dataMEM := DMEM.io.dataOut
  io.out.RegDest := io.in.RegDest

  io.outIF.NewPC := io.in.NewPC
  io.outIF.PCSel := (io.in.controlSignals.branch && io.in.BranchOut) || io.in.controlSignals.jump
}
