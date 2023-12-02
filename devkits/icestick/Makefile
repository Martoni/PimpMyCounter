NEXTPNR=nextpnr-ice40
YOSYS=yosys
ICEPACK=icepack

VERILOGDIR=../../
SCALASRCDIR=$(VERILOGDIR)/src/main/scala/
TOP=IcestickBlink
PINOUT=Blink.pcf

$(TOP).bin: $(TOP).asc
	$(ICEPACK) $< $@

$(TOP).asc: $(TOP).json $(PINOUT)
	$(NEXTPNR) --hx1k --json $(TOP).json --pcf $(PINOUT) --asc $@ --package tq144 --opt-timing 2> log_pnr.txt
	grep "Max frequency" log_pnr.txt

$(TOP).json: $(VERILOGDIR)/$(TOP).v
	$(YOSYS) -p 'synth_ice40 -top $(TOP) -json $@' $<

$(VERILOGDIR)/$(TOP).v: $(SCALASRCDIR)/$(TOP).scala
	cd $(VERILOGDIR);sbt 'runMain $(TOP)'

flash: $(TOP).bin
	openFPGALoader -b ice40_generic $(TOP).bin

clean:
	rm -f $(TOP).json $(TOP).asc $(VERILOGDIR)/$(TOP).v
