package FiveStage
import chisel3._

class IFIDBundle extends Bundle {
  val pc = UInt(32.W)
  val instruction = new Instruction
}

class IDEXBundle extends Bundle {
  val pc = UInt(32.W)
  val controlSignals = new ControlSignals
  val BranchType = UInt(3.W)
  val Op1Select = UInt(1.W)
  val Op2Select = UInt(1.W)
  val ALUop = UInt(4.W)
  val Imm = UInt(32.W)
  val RegVal1 = UInt(32.W)
  val RegVal2 = UInt(32.W)
  val RegAddr1 = UInt(5.W)
  val RegAddr2 = UInt(5.W)
  val RegDest = UInt(5.W)
  val NewPC = UInt(32.W)
}

class EXMEMBundle extends Bundle {
  val pc = UInt(32.W)
  val controlSignals = new ControlSignals
  val BranchOut = Bool()
  val ALUOut = UInt(32.W)
  val RegVal = UInt(32.W)
  val RegDest = UInt(5.W)
  val NewPC = UInt(32.W)
}

class MEMWBBundle extends Bundle {
  val controlSignals = new ControlSignals
  val ALUOut = UInt(32.W)
  val dataMEM = UInt(32.W)
  val RegDest = UInt(5.W)
}

class MEMIFBundle extends Bundle {
  val NewPC = UInt(32.W)
  val PCSel = Bool()
}

class WBIDBundle extends Bundle {
  val RegWrite = Bool()
  val Result = UInt(32.W)
  val RegDest = UInt(5.W)
}

class EXBundle extends Bundle {
  val RegVal = UInt(32.W)
  val RegDest = UInt(5.W)
  val RegWrite = Bool()
}