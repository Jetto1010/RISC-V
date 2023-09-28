package FiveStage
import chisel3._

class WriteBack extends Module {
  val io = IO(
    new Bundle {
      val in = Input(new MEMWBBundle)
      val out = Output(new WBIDBundle)
    }
  )

  io.out.RegWrite := io.wbin.RegWrite
  io.out.WriteReg := io.in.WriteReg
  io.out.Result := Mux(io.in.MemRead, io.in.ReadData, io.in.ALUOut)
}