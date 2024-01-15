# Generate Verilog:

```Scala
$ sbt
sbt:PimpMyCounter> runMain helloworldfpga
```
To select counter type, change lines in Chisel code in
[src/main/scala/QuickFeatherBlink.scala](https://github.com/Martoni/PimpMyCounter/blob/main/src/main/scala/QuickFeatherBlink.scala#L18)

```Scala
      val blink = Module(new Blink(44,
                                  LED_WIDTH=3,
//                                  COUNTTYPE=CounterTypes.NaturalCount))
//                                  COUNTTYPE=CounterTypes.FullAdderCount))
                                  COUNTTYPE=CounterTypes.PdChain))
```
Then runMain again.

File helloworldfpga.v is generated, copy it in your QORC apps directory 
(`qorc-sdk/qf_apps/qf_helloworldhw/fpga/rtl` replacing helloworldfpga.v original file) then go to `qorc-sdk/qf_apps/qf_helloworldhw/GCC_Project` directory then make.



# helpers

* [quickfeather-notes](https://github.com/trabucayre/quickfeather-notes/blob/master/FPGAFlow.md)
* [Hackable article](https://connect.ed-diamond.com/hackable/hk-040/le-premier-fpga-avec-sa-chaine-de-developpement-open-source)
* [Flashing quickfeather](https://sensiml.com/documentation/firmware/quicklogic-quickfeather/quicklogic-quickfeather.html)
