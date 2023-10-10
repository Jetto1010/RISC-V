package FiveStage
import chisel3._

class IFIDBundle extends Bundle {
  val pc = UInt(32.W)
  val instruction = new Instruction
}

class IDEXBundle extends Bundle {
  // val pc = UInt(32.W)

  val controlSignals = new ControlSignals
  val BranchType = UInt(3.W)
  val Op1Select = UInt(32.W)
  val Op2Select = UInt(32.W)
  val ALUop = UInt(4.W)
  val rd2 = UInt(32.W)
  val RegDest = UInt(5.W)
  // val SignImm = UInt(32.W)
}

class EXMEMBundle extends Bundle {
  val controlSignals = new ControlSignals
  val BranchType = UInt(3.W)
  val ALUOut = UInt(32.W)
  val rd2 = UInt(32.W)
  val RegDest = UInt(5.W)
}

class MEMWBBundle extends Bundle {
  val controlSignals = new ControlSignals
  val ALUOut = UInt(32.W)
  val dataMEM = UInt(32.W)
  val RegDest = UInt(5.W)
}

class MEMIFBundle extends Bundle {
  val PCSrc = UInt(32.W)
  val PCBranch= UInt(32.W)
}

class WBIDBundle extends Bundle {
  val RegWrite = Bool()
  val Result = UInt(32.W)
  val RegDest = UInt(5.W)
}
