package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule


class InstructionDecode extends MultiIOModule {

  // Don't touch the test harness
  val testHarness = IO(
    new Bundle {
      val registerSetup = Input(new RegisterSetupSignals)
      val registerPeek  = Output(UInt(32.W))

      val testUpdates   = Output(new RegisterUpdates)
    })


  val io = IO(
    new Bundle {
      val in = Input(new IFIDBundle)
      val wbin = Input(new WBIDBundle)

      val out = Output(new IDEXBundle)
    }
  )

  val registers = Module(new Registers)
  val decoder   = Module(new Decoder).io


  /**
    * Setup. You should not change this code
    */
  registers.testHarness.setup := testHarness.registerSetup
  testHarness.registerPeek    := registers.io.readData1
  testHarness.testUpdates     := registers.testHarness.testUpdates


  /**
    * TODO: Your code here.
    */
  registers.io.readAddress1 := io.in.instruction.registerRs1
  registers.io.readAddress2 := io.in.instruction.immediateIType
  registers.io.writeEnable  := io.wbin.RegWrite
  registers.io.writeAddress := io.in.instruction.registerRd //add writeReg
  registers.io.writeData    := io.wbin.Result

  decoder.instruction := 0.U.asTypeOf(new Instruction)

  val zeroReg = RegInit(0.U(32.W))
  io.out.RegWrite := zeroReg
  io.out.MemRead := zeroReg
  io.out.MemWrite := zeroReg
  io.out.Branch := zeroReg
  io.out.ALUSrc := zeroReg
  io.out.ALUOp := zeroReg
  io.out.RegDest := zeroReg

  io.out.SignImm :=
}
