package FiveStage

import chisel3._
import chisel3.core.Input
import chisel3.experimental.MultiIOModule
import chisel3.experimental._


class CPU extends MultiIOModule {

  val testHarness = IO(
    new Bundle {
      val setupSignals = Input(new SetupSignals)
      val testReadouts = Output(new TestReadouts)
      val regUpdates   = Output(new RegisterUpdates)
      val memUpdates   = Output(new MemUpdates)
      val currentPC    = Output(UInt(32.W))
    }
  )

  /**
    You need to create the classes for these yourself
    */
  val IFID = Module(new IFID)
  val IDEX  = Module(new IDEX)
  val EXMEM  = Module(new EXMEM)
  val MEMWB = Module(new MEMWB)

  val IF = Module(new InstructionFetch)
  val ID  = Module(new InstructionDecode)
  val EX  = Module(new Execution)
  val MEM = Module(new MemoryFetch)
  val WB  = Module(new WriteBack)


  /**
    * Setup. You should not change this code
    */
  IF.testHarness.IMEMsetup     := testHarness.setupSignals.IMEMsignals
  ID.testHarness.registerSetup := testHarness.setupSignals.registerSignals
  MEM.testHarness.DMEMsetup    := testHarness.setupSignals.DMEMsignals

  testHarness.testReadouts.registerRead := ID.testHarness.registerPeek
  testHarness.testReadouts.DMEMread     := MEM.testHarness.DMEMpeek

  /**
    spying stuff
    */
  testHarness.regUpdates := ID.testHarness.testUpdates
  testHarness.memUpdates := MEM.testHarness.testUpdates
  testHarness.currentPC  := IF.testHarness.PC


  /**
    TODO: Your code here
    */
  IFID.io.in  <> IF.io.out
  ID.io.in    <> IFID.io.out
  IDEX.io.in  <> ID.io.out
  EX.io.in    <> IDEX.io.out
  EXMEM.io.in <> EX.io.out
  MEM.io.in   <> EXMEM.io.out
  MEMWB.io.in <> MEM.io.out
  WB.io.in    <> MEMWB.io.out
  ID.io.wbin  <> WB.io.out
  IF.io.in    <> MEM.io.outIF

  // Forward 
  EX.io.memIn <> MEM.io.outEX
  EX.io.wbIn <> WB.io.outEX
}
