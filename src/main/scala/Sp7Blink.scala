import chisel3._
import chisel3.util._

class Sp7Blink extends RawModule {
  /* IO */
  val clock = IO(Input(Clock()))
  val resetn = IO(Input(Bool()))
  val leds = IO(Output(UInt(8.W)))

  /* PLL */
  val reset = Wire(Bool())
  reset := !resetn

  /* Blink */
  withClockAndReset(clock, reset){
      val blink = Module(new Blink(44,
                                  LED_WIDTH=8,
                                  COUNTTYPE=CounterTypes.NaturalCount))
//                                  COUNTTYPE=CounterTypes.FullAdderCount))
      leds := blink.io.leds
  }
}

object Sp7Blink extends App {
  println("Generate Sp7Blink")
  (new chisel3.stage.ChiselStage).emitVerilog(new Sp7Blink(), args)
}
