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
  registers.io.readAddress2 := io.in.instruction.registerRs2
  registers.io.writeEnable  := io.wbin.RegWrite
  registers.io.writeAddress := io.in.instruction.registerRd
  registers.io.writeData    := io.wbin.Result

  val imm = MuxLookup(decoder.immType, 0.S(32.W), Array(
    ImmFormat.ITYPE -> decoder.instruction.immediateIType,
    ImmFormat.STYPE -> decoder.instruction.immediateSType,
    ImmFormat.BTYPE -> decoder.instruction.immediateBType,
    ImmFormat.UTYPE -> decoder.instruction.immediateUType,
    ImmFormat.JTYPE -> decoder.instruction.immediateJType,
    ImmFormat.DC    -> 0.S(32.W)
  ))

  decoder.instruction := io.in.instruction
  io.out.controlSignals := decoder.controlSignals
  io.out.BranchType := decoder.branchType
  io.out.Op1Select := MuxLookup(decoder.op1Select, 0.U, Array(
    Op1Select.rs1 -> registers.io.readData1,
    Op1Select.PC  -> io.in.pc,
    Op1Select.DC  -> 0.U,
  ))
  io.out.Op2Select := MuxLookup(decoder.op1Select, 0.U, Array(
    Op2Select.rs2 -> registers.io.readData2,
    Op2Select.imm -> imm.asUInt(),
    Op2Select.DC  -> 0.U,
  ))
  io.out.ALUop := decoder.ALUop
  io.out.rd2 := registers.io.readData2
  io.out.WriteReg := decoder.instruction.registerRd
}
