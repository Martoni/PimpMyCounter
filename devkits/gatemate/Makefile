include config.mk

PRFLAGS +=-ccf src/$(TOP).ccf
TOP = GatemateBlink

VERILOGDIR=src
SBTDIR=../../
SCALASRCDIR=$(SBTDIR)/src/main/scala/

VLOG_SRC = src/$(TOP).v

$(VERILOGDIR)/$(TOP).v: $(SCALASRCDIR)/$(TOP).scala $(SCALASRCDIR)/Blink.scala
	cd $(SBTDIR);sbt 'runMain $(TOP)'
	mv $(SBTDIR)/$(TOP).v $(VERILOGDIR)/

mrproper:
	rm -f $(TOP).json $(TOP).asc $(VERILOGDIR)/$(TOP).v
