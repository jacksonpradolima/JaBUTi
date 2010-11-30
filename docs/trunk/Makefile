# Makefile for Jabuti 1.0 Manual

all: jabuti.pdf

jabuti.dvi: *.tex *.eps
	latex jabuti.tex && latex jabuti

jabuti.ps: jabuti.dvi
	dvips jabuti.dvi

jabuti.pdf: jabuti.ps
	ps2pdfwr jabuti.ps

clean:
	rm -f jabuti.dvi jabuti.ps jabuti.pdf

