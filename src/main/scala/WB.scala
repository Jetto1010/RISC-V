package FiveStage
import chisel3._

class WriteBack extends Module {
  val io = IO(
    new Bundle {
      val in = Input(new MEMWBBundle)
      val out = Output(new WBIFBundle)
    }
  )

  io.out.RegWrite := io.wbin.RegWrite
  io.out.Result := Mux(io.in.MemtoReg, io.in.ALUOut, io.in.ReadData)
  io.out.WriteReg := io.in.WriteReg
}