package FiveStage
import chisel3._

class WriteBack extends Module {
  val io = IO(
    new Bundle {
      val in = Input(new MEMWBBundle)
      val out = Output(new WBIDBundle)
      val outEX = Output(new EXBundle)
    }
  )

  io.out.RegWrite := io.in.controlSignals.regWrite
  io.out.RegDest := io.in.RegDest
  io.out.Result := Mux(io.in.controlSignals.memRead, io.in.dataMEM, io.in.ALUOut)

  io.outEX.RegVal := io.out.Result
  io.outEX.RegDest := io.in.RegDest
  io.outEX.RegWrite := io.in.controlSignals.regWrite
}