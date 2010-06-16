# Exports content from the repository to a file in OWL/RDF format.
#
# Options:
#   -out,--output <uri> URI or path of the directory to contain the
#        resulting XML file.  The file name will be automatically derived
#        from the coding scheme name.   
#   -u,--urn <name> URN of the coding scheme to export.
#   -v,--version <id> The assigned tag/label or absolute version
#        identifier of the coding scheme.
#   -f,--force If specified, allows the destination file to be
#        overwritten if present.
# 
# Note: If the coding scheme and version values are unspecified,
# a list of available coding schemes will be presented for
# user selection.
#
# Example: ExportOwlRdf -out "file:///path/to/dir" -f
# Example: ExportOwlRdf -out "file:///path/to/dir" -u "NCI_Thesaurus" -v "PRODUCTION" -f
#
java -Xmx2000m -cp "../runtime/lbPatch.jar:../runtime/lbRuntime.jar" org.LexGrid.LexBIG.admin.ExportOwlRdf $@