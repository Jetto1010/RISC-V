package FiveStage
import chisel3._

class IFID extends Module {
  val io = IO(
    new Bundle {
      val in = Input(new IFIDBundle)
      val out = Output(new IFIDBundle)
    }
  )

  val delay = RegInit(0.U(32.W))
  delay := io.in.pc
  io.out.pc := delay
  io.out.instruction := io.in.instruction
}

class IDEX extends Module {
  val io = IO(
    new Bundle {
      val in = Input(new IDEXBundle)
      val out = Output(new IDEXBundle)
    }
  )

  val delay = RegInit(0.U(32.W))
  delay := io.in
  io.out := io.in
}

class EXMEM extends Module {
  val io = IO(
    new Bundle {
      val in = Input(new EXMEMBundle)
      val out = Output(new EXMEMBundle)
    }
  )

  val delay = RegInit(0.U(32.W))
  delay := io.in
  io.out := io.in
}

class MEMWB extends Module {
  val io = IO(
    new Bundle {
      val in = Input(new MEMWBBundle)
      val out = Output(new MEMWBBundle)
    }
  )

  val delay = RegInit(0.U(32.W))
  delay := io.in
  io.out := io.in
}
