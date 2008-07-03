#! /usr/bin/perl  -w

open ATTRIB, "DocTypes.xml";
$hdrx = <<END;
<?xml version="1.0" encoding="UTF-8"?>
<data xmlns="ns:workflow" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="ns:workflow resource:WorkflowData">
  <documentTypes xmlns="ns:workflow/DocumentType" xsi:schemaLocation="ns:workflow/DocumentType resource:DocumentType">
END
$tlx = <<END;
    </documentType>
  </documentTypes>
</data>
END

$flag=0;
$tempPage="";
while ($_ = <ATTRIB>){
	if (/\<documentType\>/) {
		$flag=1;
		$tempPage = $tempPage.$hdrx;
	}
	if (/\<\/documentType\>/) {
		$flag=0;
		$tempPage = $tempPage.$tlx;
		open OUTFILE, '>', "./temp/".$name.".xml" or die "Couldn't open ".$name.".xml";
		print OUTFILE $tempPage;
		close OUTFILE;
		$tempPage="";
		print $name.".xml created\n";
		$name="blank";
	}
	if (/\<name\>/){
if ($name eq "blank"){
			/\>([^<]*)\</;
			$name=$1;
		}
	}
	if ($flag==1) {$tempPage = $tempPage.$_;}
}
