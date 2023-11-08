package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule

class BranchPredictor extends MultiIOModule {
    val io = IO(
        new Bundle {
            val PC = Input(UInt(32.W))
            val Update = Input(Bool())
            val Taken = Input(Bool())

            val Predict = Output(Bool())
        }
    )

    val PatternHistoryTable = Vec.fill(32){Module(new SaturatedCounter).io}

    for(i <- 0 until 32) {
        PatternHistoryTable(i).Taken := Bool(false)
        PatternHistoryTable(i).Update := Bool(false)
    }

    val address = io.PC(5, 1)
    io.Predict := PatternHistoryTable(address).Predict

    PatternHistoryTable(address).Taken := io.Taken
    PatternHistoryTable(address).Update := io.Update
} 