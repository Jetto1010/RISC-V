package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule

class BranchPredictor extends MultiIOModule {
    val io = IO(
        new Bundle {
            val PC = Input(UInt(32.W))
            val Update = Input(UInt(32.W))
            val Taken = Input(Bool())

            val Predict = Output(Bool())
        }
    )

    val PatternHistoryTable = Vec.fill(32){Module(new SaturatedCounter).io}

    io.Predict := PatternHistoryTable(io.PC(4, 0)).Predict

    when(io.Update =/= 1.U) {
        PatternHistoryTable(io.Update(4, 0)).Update := io.Taken
    }
} 