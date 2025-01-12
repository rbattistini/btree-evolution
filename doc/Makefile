## Targets defined are:
#
# make			  same as make doc
# make doc		  compile the report both in pdf and html format
# make pdf		  compile the report in pdf format
# make web		  compile the report in html format
# make cldocs	  rimuove il pdf generato

DOCS       := cldoc doc

.PHONY: $(DOCS)

doc: pdf web

pdf: ./*.adoc
	asciidoctor-pdf -r asciidoctor-diagram -r asciidoctor-bibtex  ./report.adoc  -o ./output/report.pdf

web: ./*.adoc
	asciidoctor -r asciidoctor-diagram -r asciidoctor-bibtex ./report.adoc -o ./output/report.html

cldoc:
	rm - f ./output/*.pdf ./output/.asciidoc/*
