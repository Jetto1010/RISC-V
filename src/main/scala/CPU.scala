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

  val IF  = Module(new InstructionFetch)
  val ID  = Module(new InstructionDecode)
  val EX  = Module(new Execution)
  val MEM = Module(new MemoryFetch)
  val WB  = Module(new WriteBack)
  val BP  = Module(new BranchPredictor)


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
  IF.io.in    <> MEM.io.outIF
  IFID.io.in  <> IF.io.out
  ID.io.in    <> IFID.io.out
  IDEX.io.in  <> ID.io.out
  EX.io.in    <> IDEX.io.out
  EXMEM.io.in <> EX.io.out
  MEM.io.in   <> EXMEM.io.out
  MEMWB.io.in <> MEM.io.out
  WB.io.in    <> MEMWB.io.out
  ID.io.wbin  <> WB.io.out

  // Forward 
  EX.io.memIn <> MEM.io.outEX
  EX.io.wbIn  <> WB.io.outEX

  // Stall and squash
  val squash = RegInit(Bool(), false.B)
  val squash2 = RegInit(Bool(), false.B)
  val stall = Wire(Bool())
  stall := ID.io.stall || EX.io.out.BranchOut || squash || squash2 || IDEX.io.out.BranchPredicted
  IF.io.stall   := ID.io.stall 
  IDEX.io.stall := stall 
  ID.io.squash := stall
  squash := EX.io.out.BranchOut
  squash2 := squash || IDEX.io.out.BranchPredicted

  // Branch Prediction
  BP.io.PC := IFID.io.out.pc
  ID.io.branchPredict := BP.io.Predict
  BP.io.UpdatePC := EXMEM.io.out.pc
  BP.io.Taken := EXMEM.io.out.BranchTaken
  BP.io.Update := EXMEM.io.out.controlSignals.branch 

  when(MEM.io.outIF.PCSel) {
    IF.io.in <> MEM.io.outIF
  }.elsewhen(IDEX.io.out.BranchPredicted) {
    IF.io.in <> IDEX.io.out.IFBundle
  }
  // Branch Prediction Checker
  val number   = RegInit(UInt(12.W), 0.U)
  val right    = RegInit(UInt(12.W), 0.U)
  val notTaken = RegInit(UInt(12.W), 0.U)

  when(EXMEM.io.out.controlSignals.branch) {
    when(!EXMEM.io.out.BranchOut) {
      right := right + 1.U
    }
    when(!EXMEM.io.out.BranchTaken) {
      notTaken := notTaken + 1.U
    }
    number := number + 1.U
  }

  when(number === 1738.U && EXMEM.io.out.controlSignals.branch) { // 1738 is the number of branches - 1 in branchProfiler
    printf("Number: %d | Right %d | Not Taken: %d \n", number, right, notTaken)
  }
}
