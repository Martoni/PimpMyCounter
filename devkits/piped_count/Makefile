NEXTPNR=nextpnr-ice40
YOSYS=yosys

VERILOGDIR=../../
SCALASRCDIR=$(VERILOGDIR)/src/main/scala/
TOP=Blink
PINOUT=Blink.pcf

$(TOP).asc: $(TOP).json $(PINOUT)
	$(NEXTPNR) --hx1k --json $(TOP).json --pcf $(PINOUT) --asc $@ --package tq144 2> log_pnr.txt

$(TOP).json: $(VERILOGDIR)/$(TOP).v
	$(YOSYS) -p 'synth_ice40 -top $(TOP) -json $@' $<

$(VERILOGDIR)/$(TOP).v: $(SCALASRCDIR)/$(TOP).scala
	cd $(VERILOGDIR);sbt 'runMain $(TOP)'

clean:
	rm -f $(TOP).json $(TOP).asc $(VERILOGDIR)/$(TOP).v
