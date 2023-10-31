package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase, MuxLookup }
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
      val stall = Output(Bool())
    }
  )

  val registers = Module(new Registers)
  val decoder   = Module(new Decoder).io

  val PreviousMemRead = RegInit(Bool(), Bool(false))
  val PreviousRegDest = RegInit(UInt(5.W), 0.U)

  /**RegDest
    * Setup. You should not change this code
    */
  registers.testHarness.setup := testHarness.registerSetup
  testHarness.registerPeek    := registers.io.readData1
  testHarness.testUpdates     := registers.testHarness.testUpdates


  /**
    * TODO: Your code here.
    */
  registers.io.readAddress1 := io.in.instruction.registerRs1
  registers.io.readAddress2 := io.in.instruction.registerRs2
  registers.io.writeEnable  := io.wbin.RegWrite
  registers.io.writeAddress := io.wbin.RegDest
  registers.io.writeData    := io.wbin.Result

  decoder.instruction := io.in.instruction
  io.out.Imm :=  MuxLookup(decoder.immType, 0.S(32.W), Array(
    ImmFormat.ITYPE -> decoder.instruction.immediateIType,
    ImmFormat.STYPE -> decoder.instruction.immediateSType,
    ImmFormat.BTYPE -> decoder.instruction.immediateBType,
    ImmFormat.UTYPE -> decoder.instruction.immediateUType,
    ImmFormat.JTYPE -> decoder.instruction.immediateJType,
    ImmFormat.DC    -> 0.S(32.W)
  )).asUInt

  io.out.pc := io.in.pc
  io.stall := PreviousMemRead && (PreviousRegDest === io.in.instruction.registerRs1 || PreviousRegDest === io.in.instruction.registerRs2)
  io.out.RegVal1 := Mux(io.wbin.RegDest === io.in.instruction.registerRs1 && io.wbin.RegWrite, io.wbin.Result, registers.io.readData1)
  io.out.RegVal2 := Mux(io.wbin.RegDest === io.in.instruction.registerRs2 && io.wbin.RegWrite, io.wbin.Result, registers.io.readData2)
  PreviousMemRead := io.out.controlSignals.memRead
  PreviousRegDest := io.out.RegDest

  when(io.stall) {
    io.out.controlSignals := ControlSignals.nop
    io.out.BranchType := branchType.DC
    io.out.Op1Select := Op1Select.DC
    io.out.Op2Select := Op2Select.DC
    io.out.ALUop := ALUOps.DC
    io.out.RegAddr1 := 0.U
    io.out.RegAddr2 := 0.U
    io.out.RegDest := 0.U
  }.otherwise{
    io.out.controlSignals := decoder.controlSignals
    io.out.BranchType := decoder.branchType
    io.out.Op1Select := decoder.op1Select
    io.out.Op2Select := decoder.op2Select
    io.out.ALUop := decoder.ALUop
    io.out.RegAddr1 := io.in.instruction.registerRs1
    io.out.RegAddr2 := io.in.instruction.registerRs2
    io.out.RegDest := decoder.instruction.registerRd
  }
}


