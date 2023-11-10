package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule

class BranchPredictor extends MultiIOModule {
    val io = IO(
        new Bundle {
            val PC = Input(UInt(32.W))
            val UpdatePC = Input(UInt(32.W))
            val Update = Input(Bool())
            val Taken = Input(Bool())

            val Predict = Output(Bool())
        }
    )

    val PatternHistoryTable = Vec.fill(32){Module(new SaturatedCounter).io}

    for(i <- 0 until 32) {
        PatternHistoryTable(i).Taken := false.B
        PatternHistoryTable(i).Update := false.B
    }

    io.Predict := PatternHistoryTable(io.PC(5, 1)).Predict

    PatternHistoryTable(io.UpdatePC(5, 1)).Taken := io.Taken
    PatternHistoryTable(io.UpdatePC(5, 1)).Update := io.Update
} 