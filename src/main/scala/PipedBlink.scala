// Implementation naïve d'un blinker
import chisel3._
import chisel3.util._

class PipedBlink(val LED_FREQ: Double = 0.5,
            val CLK_FREQ: Int = 100000000) extends Module {
  val io = IO(new Bundle{
      val led = Output(Bool())
  })

  val (counterValue, counterWrap) = Counter(true.B, (CLK_FREQ/LED_FREQ).toInt)
  io.led := false.B
  when(counterValue <= (CLK_FREQ/2).U){
    io.led := true.B
  }

}

object PipedBlink extends App {
  (new chisel3.stage.ChiselStage).emitVerilog(new PipedBlink, args)
}
