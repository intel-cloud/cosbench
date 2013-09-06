        echo waiting for $1 in ${2}/stop file
 
	cnt=0
        while [ 1 ]
        do
                sleep 1
		let cnt=$cnt+1
                echo -n .
                grep $1 ${2}/stop
                if [ $? -eq 0 ]
                then
                        echo found $1
                        break
                fi
		if [ $cnt -gt 2000 ];
		then
			echo "timeout"
			echo "canceling cosbench run, canceling $2, runid is $1"
			sh $2/cli.sh cancel $1
		fi
        done

