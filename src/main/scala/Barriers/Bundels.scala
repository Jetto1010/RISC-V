package FiveStage
import chisel3._

class IFIDBundle extends Bundle {
  val pc = UInt(32.w)
  val instruction = new Instruction
}

class IDEXBundle extends Bundle {
  val pc = UInt(32.w)
  val op1 = UInt(32.w) // SrcA
  val op2 = UInt(32.w) // SrcB
  val ALUControl = UInt(7.w) // opcode

  val RegWrite = UInt(32.w)
  val MemtoReg = UInt(32.w)
  val MemWrite = UInt(32.w)
  val Branch = UInt(32.w)
  val ALUSrc = UInt(32.w)
  val RegDest = UInt(32.w)
  // val Rt = UInt(32.w)
  // val Rd = UInt(32.w)
  val SignImm = UInt(32.w)
}

class EXMEMBundle extends Bundle {
  val pc = UInt(32.w)
  val ALUOut = UInt(32.w)
  val RegWrite = UInt(32.w)
  val MemtoReg = UInt(32.w)
  val MemWrite = UInt(32.w)
  val Branch = UInt(32.w)
  val Zero = UInt(32.w)
  val ALUOut = UInt(32.w)
  val WriteData = UInt(32.w)
  val WriteReg = UInt(32.w)
}

class MEMWBBundle extends Bundle {
  val RegWrite = UInt(32.w)
  val MemtoReg = UInt(32.w)
  val ALUOut = UInt(32.w)
  val ReadData = UInt(32.w)
  val WriteReg = UInt(32.w)
}

class MEMIFBundle extends Bundle {
  val PCSrc = UInt(32.w)
  val PCBranch= UInt(32.w)
}

class WBIDBundle extends Bundle {
  val RegWrite = UInt(32.w)
  val WriteReg = UInt(32.w)
  val Result = UInt(32.w)
}
