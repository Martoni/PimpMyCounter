nextpnr-ice40 --hx1k --json Blink.json --pcf icestick.pcf --asc Blink.asc 2> log.txt
yosys -p 'synth_ice40 -top Blink -json Blink.json' ../../IcestickBlink.v 
