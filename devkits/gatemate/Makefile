include config.mk

PRFLAGS +=-ccf src/$(TOP).ccf
#  -om <arg>,      --fpga_mode <arg>           operation mode {1:lowpower, 2:economy, 3:speed} (default: 3)
PRFLAGS +=--fpga_mode 3
#  -tm <arg>,      --time_mode <arg>           timing mode {1:best, 2:typical, 3:worst} (default: 3)
PRFLAGS +=-tm 1

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
