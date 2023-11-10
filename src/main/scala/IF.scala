package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule

class InstructionFetch extends MultiIOModule {

  // Don't touch
  val testHarness = IO(
    new Bundle {
      val IMEMsetup = Input(new IMEMsetupSignals)
      val PC        = Output(UInt())
    }
  )

  /**
    * TODO: Add input signals for handling events such as jumps

    * TODO: Add output signal for the instruction. 
    * The instruction is of type Bundle, which means that you must
    * use the same syntax used in the testHarness for IMEM setup signals
    * further up.
    */
  val io = IO(
    new Bundle {
      val in = Input(new IFBundle)
      val stall = Input(Bool())
      val fromID = Input(Bool())

      val out = Output(new IFIDBundle)
    })

  val IMEM = Module(new IMEM)
  val PC   = RegInit(UInt(32.W), 0.U)
  val Old  = RegInit(UInt(32.W), 0.U)

  /**
    * Setup. You should not change this code
    */
  IMEM.testHarness.setupSignals := testHarness.IMEMsetup
  testHarness.PC := IMEM.testHarness.requestedAddress


  /**
    * TODO: Your code here. 
    * 
    * You should expand on or rewrite the code below.
    */
  when(io.fromID) {
    printf("NewPC: %d \n", io.in.NewPC)
  }
  io.out.pc := Mux(io.stall, Old, PC)

  IMEM.io.instructionAddress := io.out.pc 
  val instruction = Wire(new Instruction)
  instruction := IMEM.io.instruction.asTypeOf(new Instruction)
  io.out.instruction := instruction
  
  PC := Mux(io.in.PCSel, io.in.NewPC, PC + Mux(io.stall, 0.U, 4.U))
  Old := PC

  /**
    * Setup. You should not change this code.
    */
  when(testHarness.IMEMsetup.setup) {
    PC := 0.U
    instruction := Instruction.NOP
  }
}
