// Blink icestick LED
//
// Pour calculer les paramètres de la PLL, le projet icestorm propose l'outils
// icepll. Si on veut une horloge de sortie à 275Mhz il suffit de lancer la
// commande :
// $ icepll -o 275
//
//F_PLLIN:    12.000 MHz (given)
//F_PLLOUT:  275.000 MHz (requested)
//F_PLLOUT:  276.000 MHz (achieved)
//
//FEEDBACK: SIMPLE
//F_PFD:   12.000 MHz
//F_VCO:  552.000 MHz
//
//DIVR:  0 (4'b0000)   
//DIVF: 45 (7'b0101101)
//DIVQ:  1 (3'b001)    
//
//FILTER_RANGE: 1 (3'b001)
//
import java.lang.Boolean
import chisel3._
import chisel3.util._
import chisel3.experimental.Param
import fpgamacro.ice40.SB_PLL40_CORE

class IcestickBlink(val use_pll: Boolean = false) extends RawModule {
  /* IO */
  val clk      = IO(Input(Clock()))
  val led      = IO(Output(Bool()))
  val red_leds = IO(Output(UInt(4.W)))

  if (use_pll) { 
    println("Generate IcestickBlink using PLL")
    val pm: Map[String, Param] = Map(
     "FEEDBACK_PATH" -> "SIMPLE",
     "DELAY_ADJUSTMENT_MODE_FEEDBACK" -> "FIXED",
     "DELAY_ADJUSTMENT_MODE_RELATIVE" -> "FIXED",
     "SHIFTREG_DIV_MODE" -> 0,
     "FDA_FEEDBACK" -> 0,
     "FDA_RELATIVE" -> 0,
     "PLLOUT_SELECT" -> "GENCLK",
     "DIVR" -> 0,
     "DIVF" -> 45,
     "DIVQ" -> 1,
     "FILTER_RANGE" -> 0,
     "ENABLE_ICEGATE" -> 0,
     "TEST_MODE" -> 0,
     "EXTERNAL_DIVIDE_FACTOR" -> 1)

    val clock = Wire(Clock())
    val gm_pll = Module(new SB_PLL40_CORE(pm))
    gm_pll.io.REFERENCECLK := clk
    clock := gm_pll.io.PLLOUTCORE
    gm_pll.io.RESETB := true.B
    gm_pll.io.BYPASS := false.B

    withClockAndReset(clock, false.B){
      val blink = Module(new Blink(32, COUNTTYPE=CounterTypes.PdChain))
      led := blink.io.leds(7)
      red_leds := blink.io.leds(6,3)
    }
  } else {
    println("Generate IcestickBlink with direct clock")
    withClockAndReset(clk, false.B){
//      val blink = Module(new Blink(24, NaturalCount=true))
      val blink = Module(new Blink(44,
                                  LED_WIDTH=5,
//                                  COUNTTYPE=CounterTypes.NaturalCount))
//                                  COUNTTYPE=CounterTypes.FullAdderCount))
                                  COUNTTYPE=CounterTypes.FullPGAdderCount))
//                                  COUNTTYPE=CounterTypes.PdChain))
      led := blink.io.leds(4)
      red_leds := blink.io.leds(3,0)
    }
  }
}

object IcestickBlink extends App {
  var use_pll: Boolean = false;

  if (args.length > 0) {
    use_pll = Boolean.parseBoolean(args(0))
  } 
  (new chisel3.stage.ChiselStage).emitVerilog(new IcestickBlink(use_pll), args)
}
