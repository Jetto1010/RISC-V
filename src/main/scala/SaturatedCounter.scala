package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule

class SaturatedCounter extends MultiIOModule {
    val io = IO(
        new Bundle{
            val Update = Input(Bool())

            val Predict = Output(Bool())
        }
    )
    val Counter = RegInit(UInt(2.W), 0.U)

    when(io.Update && Counter =/= 3.U) {
        Counter := Counter + 1.U
    }.elsewhen(!io.Update && Counter =/= 0.U) {
        Counter := Counter - 1.U
    }.otherwise {
        Counter := Counter
    }

    io.Predict := Mux(Counter >= 2.U, Bool(true), Bool(false))
}