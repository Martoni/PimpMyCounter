## config.mk

EXE =

## toolchain
TOOLMAINPATH = /opt/gatemate/cc-toolchain-linux
YOSYS = $(TOOLMAINPATH)/bin/yosys/yosys$(EXE)
PR    = $(TOOLMAINPATH)/bin/p_r/p_r$(EXE)
OFL   = $(TOOLMAINPATH)/bin/openFPGALoader/openFPGALoader$(EXE)

GTKW = gtkwave
IVL = iverilog
VVP = vvp
IVLFLAGS = -Winfloop -g2012 -gspecify -Ttyp

## simulation libraries
CELLS_SYNTH = $(TOOLMAINPATH)/bin/yosys/share/gatemate/cells_sim.v
CELLS_IMPL  = $(TOOLMAINPATH)/bin/p_r/cpelib.v

## target sources
VLOG_SRC = src/GatemateBlink.v 
VHDL_SRC = $(shell find ./src/ -type f \( -iname \*.vhd -o -iname \*.vhdl \))

## misc tools
RM = rm -rf

## toolchain targets
synth: synth_vlog

synth_vlog: $(VLOG_SRC)
	$(YOSYS) -qql log/synth.log -p 'read -sv $^; synth_gatemate -top $(TOP) -nomx8 -vlog net/$(TOP)_synth.v'

synth_vhdl: $(VHDL_SRC)
	$(YOSYS) -ql log/synth.log -p 'ghdl --warn-no-binding -C --ieee=synopsys $^ -e $(TOP); synth_gatemate -top $(TOP) -nomx8 -vlog net/$(TOP)_synth.v'

impl:
	$(PR) -v -i net/$(TOP)_synth.v -o $(TOP) $(PRFLAGS) > log/$@.log

jtag:
	$(OFL) $(OFLFLAGS) -b gatemate_evb_jtag $(TOP)_00.cfg

jtag-flash:
	$(OFL) $(OFLFLAGS) -b gatemate_evb_jtag -f --verify $(TOP)_00.cfg

spi:
	$(OFL) $(OFLFLAGS) -b gatemate_evb_spi -m $(TOP)_00.cfg

spi-flash:
	$(OFL) $(OFLFLAGS) -b gatemate_evb_spi -f --verify $(TOP)_00.cfg

all: synth impl jtag

## verilog simulation targets
vlog_sim.vvp:
	$(IVL) $(IVLFLAGS) -o sim/$@ $(VLOG_SRC) sim/$(TOP)_tb.v

synth_sim.vvp:
	$(IVL) $(IVLFLAGS) -o sim/$@ net/$(TOP)_synth.v sim/$(TOP)_tb.v $(CELLS_SYNTH)

impl_sim.vvp:
	$(IVL) $(IVLFLAGS) -o sim/$@ $(TOP)_00.v sim/$(TOP)_tb.v $(CELLS_IMPL)

.PHONY: %sim %sim.vvp
%sim: %sim.vvp
	$(VVP) -N sim/$< -lx2
	@$(RM) sim/$^

wave:
	$(GTKW) sim/$(TOP)_tb.vcd sim/config.gtkw

clean:
	$(RM) log/*.log
	$(RM) net/*_synth.v
	$(RM) *.history
	$(RM) *.txt
	$(RM) *.refwire
	$(RM) *.refparam
	$(RM) *.refcomp
	$(RM) *.pos
	$(RM) *.pathes
	$(RM) *.path_struc
	$(RM) *.net
	$(RM) *.id
	$(RM) *.prn
	$(RM) *_00.v
	$(RM) *.used
	$(RM) *.sdf
	$(RM) *.place
	$(RM) *.pin
	$(RM) *.cfg*
	$(RM) *.cdf
	$(RM) sim/*.vcd
	$(RM) sim/*.vvp
	$(RM) sim/*.gtkw
