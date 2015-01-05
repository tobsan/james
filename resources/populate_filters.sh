#!/bin/bash

#
# This script creates all filter entries needed by James. Filters are used
# to assign a distributor to a certain subscriber based on their zip code.

# Differente distributors cover different zip codes, hence the need for
# these filters.
#
# Note: Bring covers a LOT of zip codes. The output of this script is about
# 1.6 MB in size. The database setup of these filters could probably be smarter
# but this is the simplest solution, and makes for easy lookup in the table.
#

FILTERFILE="setup_filters.sql"

#
# VTD ZIP CODES
#
start=(30224 30591 31020 31130 31230 31330 31431 41100 42100 42300 42400 42530
       42650 42720 42820 42911 43010 43120 43210 43330 43430 43530 43630 43730
       43830 43930 44130 44230 44330 44430 44532 44632 44730 44830 44891 44930
       45130 45230 45330 45430 45530 45631 45730 45830 45930 46011 46130 46230
       46330 46430 46530 46630 46730 46830 47130 47231 47331 47431 47531 51131
       51250 51332 51431 51532 51630 51733 51830 51930 52010 52024 52330 52430
       53030 53130 53430 53530 53591)

stop=(30279 30595 31083 31199 31298 31395 31498 41879 42280 42373 42491 42565
      42680 42751 42881 42980 43097 43190 43296 43389 43498 43588 43684 43793
      43894 43994 44196 44298 44396 44497 44557 44697 44795 44850 44896 44990
      45198 45297 45393 45495 45598 45694 45797 45897 45994 46012 46199 46297
      46395 46495 46598 46695 46796 46890 47198 47297 47399 47496 47553 51199
      51295 51397 51495 51593 51696 51796 51891 51997 52011 52026 52399 52496
      53030 53199 53496 53530 53593)

counteridx=0
counterlength=${#start[@]}
while [ $counteridx -lt $counterlength ]; do
    counter=${start[$counteridx]}
    stop=${stop[$counteridx]};
    while [ $counter -lt $((stop+1)) ]; do
        echo "INSERT INTO Filter VALUES($counter, \"VTD\");" >> $FILTERFILE
        counter=$((counter+1))
    done
    counteridx=$((counteridx+1))
done

#
# Bring ZIP CODES
#
start=(10000 20000 40000 47500 62000 63000 70000 74000)
stop=(19999 26999 44999 47599 62499 64999 73999 76999)

counteridx=0
counterlength=${#start[@]}
while [ $counteridx -lt $counterlength ]; do
    counter=${start[$counteridx]}
    stop=${stop[$counteridx]};
    while [ $counter -lt $((stop+1)) ]; do
        echo "INSERT INTO Filter VALUES($counter, \"BRING\");" >> $FILTERFILE
        counter=$((counter+1))
    done
    counteridx=$((counteridx+1))
done

# 
# TB ZIP CODES
#
start=(50010 50100 50120 50177 50230 50251)
stop=(50015 50112 50123 50190 50250 50266)
counteridx=0
counterlength=${#start[@]}
while [ $counteridx -lt $counterlength ]; do
    counter=${start[$counteridx]}
    stop=${stop[$counteridx]};
    while [ $counter -lt $((stop+1)) ]; do
        echo "INSERT INTO Filter VALUES($counter, \"TB\");" >> $FILTERFILE
        counter=$((counter+1))
    done
    counteridx=$((counteridx+1))
done

# Rest of TB zip codes
rest=(50000 50002 50004 50005 50007 50020 50115 50117 50125 50126 50127 50211 50278 50279 50282 50300 50301 50305 50306 50307 50308 50309 50310 50311
      50312 50313 50314 50315 50316 50317 50318 50319 50330 50331 50332 50333 50334 50335 50336 50337 50338 50342 50343 50400 50401 50402 50403 50404
      50405 50407 50408 50409 50410 50411 50412 50415 50418 50419 50420 50430 50431 50432 50433 50434 50435 50437 50438 50439 50441 50442 50443 50444
      50445 50446 50447 50450 50451 50452 50453 50454 50455 50456 50457 50458 50460 50461 50462 50463 50465 50466 50467 50468 50470 50471 50472 50475
      50478 50480 50481 50482 50483 50490 50491 50492 50493 50494 50495 50496 50497 50590 50591 50592 50593 50594 50595 50600 50601 50602 50603 50630
      50631 50632 50634 50635 50636 50637 50638 50639 50640 50641 50642 50643 50644 50646 50647 50648 50649 50670 50700 50701 50710 50711 50715 50720
      50730 50731 50732 50733 50734 50740 50741 50742 50743 50744 50745 50750 50751 50752 50753 50760 50761 50762 50763 50764 50765 50770 50771 50790
      50800 50801 50802 50803 50804 50805 50806 50807 50808 50809 50810 50811 50812 50813 50814 50815 50816 50817 50818 50819 50820 50821 50822 50823
      50824 50825 50826 50827 50828 50829 50830 50831 50832 50833 50834 50835 50836 50837 50838 50839 50840 50841 50842 50843 50844 50845 50846 50847
      50848 50849 50850 50851 50852 50853 50854 50855 50856 50857 50858 50859 50860 50861 50862 50863 50864 50865 50866 50867 50868 50869 50870 50871
      50872 50873 50874 50875 50876 50877 50878 50879 50880 50881 50882 50883 50884 50885 50886 50887 50888 50889 50890 50891 50892 50893 50894 50895
      50896 50897 50898 50899 51013 51020 51021 51022 51033 51040 51041 51045 51052 51053 51054 51060 51062 51090 51091 51094 51095 51097 51100 51101
      51102 51103 51104 51105 51106 51110 51120 51121 51122 51123 51124 51131 51141 51142 51143 51154 51155 51156 51157 51158 51159 51161 51162 51163
      51166 51168 51169 51170 51171 51172 51173 51174 51180 51181 51182 51191 51192 51193 51195 51196 51197 51198 51199 51200 51201 51202 51203 51204
      51220 51221 51222 51223 51224 51250 51251 51252 51253 51254 51260 51261 51262 51263 51264 51265 51276 51277 51280 51281 51283 51284 51285 51290
      51291 51292 51293 51294 51295 51300 51301 51302 51311 51312 51320 51321 51332 51333 51334 51335 51350 51370 51380 51381 51382 51391 51392 51393
      51394 51395 51396 51397 51400 51401 51403 51404 51405 51406 51411 51412 51420 51421 51422 51423 51424 51431 51432 51433 51434 51435 51440 51441
      51442 51443 51444 51450 51452 51453 51454 51455 51460 51461 51462 51463 51470 51480 51481 51482 51483 51484 51491 51492 51493 51494 51495 51500
      51501 51502 51503 51520 51521 51522 51532 51533 51534 51535 51560 51561 51570 51591 51592 51593 51600 51601 51602 51603 51620 51621 51622 51630
      51631 51632 51633 51634 51650 51677 51680 51681 51690 51691 51692 51693 51694 51695 51696 51700 51701 51702 51720 51721 51722 51723 51733 51734
      51735 51736 51737 51770 51771 51780 51781 51782 51783 51791 51792 51793 51794 51795 51796 51800 51801 51802 51820 51821 51822 51830 51831 51832
      51833 51840 51841 51842 51843 51890 51891 51902 51994 51995 51996 52010 52011 52015 52024 52025 52026 52030 52300 52301 52302 52303 52304 52320
      52321 52322 52323 52324 52325 52326 52330 52331 52332 52333 52335 52336 52337 52338 52339 52372 52374 52380 52384 52385 52386 52390 52391 52392
      52393 52394 52395 52396 52397 52398 52399 52402 52441 52442 52480 52495 52496)

for i in "${rest[@]}"; do
    echo "INSERT INTO Filter VALUES($i, \"TB\");" >> $FILTERFILE
done
