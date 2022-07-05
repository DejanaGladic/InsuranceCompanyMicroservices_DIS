# Sample usage:
#
#   HOST=localhost PORT=7000 ./test-em-all.bash
#
: ${HOST=localhost}
: ${PORT=8443}
: ${INCP_ID_EMP_INSOFF_TRAN=1}
: ${INCP_ID_NOT_FOUND=13}
: ${INCP_ID_NO_EMP=113}
: ${INCP_ID_NO_INSOFF=213}
: ${INCP_ID_NO_TRAN=313}

function assertCurl() {

  local expectedHttpCode=$1
  local curlCmd="$2 -w \"%{http_code}\""
  local result=$(eval $curlCmd)
  local httpCode="${result:(-3)}"
  RESPONSE='' && (( ${#result} > 3 )) && RESPONSE="${result%???}"

  if [ "$httpCode" = "$expectedHttpCode" ]
  then
    if [ "$httpCode" = "200" ]
    then
      echo "Test OK (HTTP Code: $httpCode)"
    else
      echo "Test OK (HTTP Code: $httpCode, $RESPONSE)"
    fi
  else
      echo  "Test FAILED, EXPECTED HTTP Code: $expectedHttpCode, GOT: $httpCode, WILL ABORT!"
      echo  "- Failing command: $curlCmd"
      echo  "- Response Body: $RESPONSE"
      exit 1
  fi
}

function assertEqual() {

  local expected=$1
  local actual=$2

  if [ "$actual" = "$expected" ]
  then
    echo "Test OK (actual value: $actual)"
  else
    echo "Test FAILED, EXPECTED VALUE: $expected, ACTUAL VALUE: $actual, WILL ABORT"
    exit 1
  fi
}
set -e

echo "HOST=${HOST}"
echo "PORT=${PORT}"

function testUrl() {
    url=$@
    if curl $url -ks -f -o /dev/null
    then
          echo "Ok"
          return 0
    else
          echo -n "not yet"
          return 1
    fi;
}

function waitForService() {
    url=$@
    echo -n "Wait for: $url... "
    n=0
    until testUrl $url
    do
        n=$((n + 1))
        if [[ $n == 100 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 6
            echo -n ", retry #$n "
        fi
    done
}

function testCompositeCreated() {

    # Expect that the INCP Composite for inCpId $INCP_ID_REVS_RECS has been created with three emp and three trans and three insOff
    if ! assertCurl 200 "curl http://$HOST:$PORT/insurance-company-composite/$INCP_ID_EMP_INSOFF_TRAN -s"
    then
        echo -n "FAIL"
        return 1
    fi

    set +e
    assertEqual "$INCP_ID_EMP_INSOFF_TRAN" $(echo $RESPONSE | jq .insuranceCompanyId)
    if [ "$?" -eq "1" ] ; then return 1; fi

    assertEqual 3 $(echo $RESPONSE | jq ".employees | length")
    if [ "$?" -eq "1" ] ; then return 1; fi

    assertEqual 3 $(echo $RESPONSE | jq ".insuranceOffers | length")
    if [ "$?" -eq "1" ] ; then return 1; fi

    assertEqual 3 $(echo $RESPONSE | jq ".transactions | length")
    if [ "$?" -eq "1" ] ; then return 1; fi

    set -e
}

function waitForMessageProcessing() {
    echo "Wait for messages to be processed... "

    # Give background processing some time to complete...
    sleep 1

    n=0
    until testCompositeCreated
    do
        n=$((n + 1))
        if [[ $n == 40 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 6
            echo -n ", retry #$n "
        fi
    done
    echo "All messages are now processed!"
}

function recreateComposite() {
    local insuranceCompanyId=$1
    local composite=$2

    assertCurl 200 "curl -X DELETE http://$HOST:$PORT/insurance-company-composite/${insuranceCompanyId} -s"
    curl -X POST http://$HOST:$PORT/insurance-company-composite -H "Content-Type: application/json" --data "$composite"
}

function setupTestdata() {

    body="{\"insuranceCompanyId\":$INCP_ID_NO_INSOFF"
    body+=\
',"name":"insurance company 1","city":"city 1","address":"address 1", "phoneNumber":"phoneNumber 1", "employees":[
        {"employeeId":1,"name":"name 1","surname":"surname 1","specialization":"specialization 1"},
        {"employeeId":2,"name":"name 2","surname":"surname 2","specialization":"specialization 2"},
        {"employeeId":3,"name":"name 3","surname":"surname 3","specialization":"specialization 3"}], "transactions":[
        {"transactionId":1,"typeTransaction":"typeTransaction 1","amount":500.50,"currencyTransaction":"currencyTransaction 1","policyNumber":"policyNumber 1"},
        {"transactionId":2,"typeTransaction":"typeTransaction 2","amount":550.50,"currencyTransaction":"currencyTransaction 2","policyNumber":"policyNumber 2"},
        {"transactionId":3,"typeTransaction":"typeTransaction 3","amount":600.50,"currencyTransaction":"currencyTransaction 3","policyNumber":"policyNumber 3"}
]}'
    recreateComposite "$INCP_ID_NO_INSOFF" "$body"

    body="{\"insuranceCompanyId\":$INCP_ID_NO_EMP"
    body+=\
',"name":"insurance company 1","city":"city 1","address":"address 1", "phoneNumber":"phoneNumber 1", "insuranceOffers":[
        {"insuranceOfferId":1,"offerName":"offerName 1","price":500.50,"currencyOffer":"currencyOffer 1"},
        {"insuranceOfferId":2,"offerName":"offerName 2","price":550.50,"currencyOffer":"currencyOffer 2"},
        {"insuranceOfferId":3,"offerName":"offerName 3","price":600.50,"currencyOffer":"currencyOffer 3"}], "transactions":[
        {"transactionId":1,"typeTransaction":"typeTransaction 1","amount":500.50,"currencyTransaction":"currencyTransaction 1","policyNumber":"policyNumber 1"},
        {"transactionId":2,"typeTransaction":"typeTransaction 2","amount":550.50,"currencyTransaction":"currencyTransaction 2","policyNumber":"policyNumber 2"},
        {"transactionId":3,"typeTransaction":"typeTransaction 3","amount":600.50,"currencyTransaction":"currencyTransaction 3","policyNumber":"policyNumber 3"}
]}'
  recreateComposite "$INCP_ID_NO_EMP" "$body"

      body="{\"insuranceCompanyId\":$INCP_ID_NO_TRAN"
      body+=\
',"name":"insurance company 1","city":"city 1","address":"address 1", "phoneNumber":"phoneNumber 1", "employees":[
        {"employeeId":1,"name":"name 1","surname":"surname 1","specialization":"specialization 1"},
        {"employeeId":2,"name":"name 2","surname":"surname 2","specialization":"specialization 2"},
        {"employeeId":3,"name":"name 3","surname":"surname 3","specialization":"specialization 3"}], "insuranceOffers":[
        {"insuranceOfferId":1,"offerName":"offerName 1","price":500.50,"currencyOffer":"currencyOffer 1"},
        {"insuranceOfferId":2,"offerName":"offerName 2","price":550.50,"currencyOffer":"currencyOffer 2"},
        {"insuranceOfferId":3,"offerName":"offerName 3","price":600.50,"currencyOffer":"currencyOffer 3"}
]}'
    recreateComposite "$INCP_ID_NO_TRAN" "$body"


    body="{\"insuranceCompanyId\":$INCP_ID_EMP_INSOFF_TRAN"
    body+=\
',"name":"insurance company 1","city":"city 1","address":"address 1", "phoneNumber":"phoneNumber 1", "employees":[
        {"employeeId":1,"name":"name 1","surname":"surname 1","specialization":"specialization 1"},
        {"employeeId":2,"name":"name 2","surname":"surname 2","specialization":"specialization 2"},
        {"employeeId":3,"name":"name 3","surname":"surname 3","specialization":"specialization 3"}], "insuranceOffers":[
        {"insuranceOfferId":1,"offerName":"offerName 1","price":500.50,"currencyOffer":"currencyOffer 1"},
        {"insuranceOfferId":2,"offerName":"offerName 2","price":550.50,"currencyOffer":"currencyOffer 2"},
        {"insuranceOfferId":3,"offerName":"offerName 3","price":600.50,"currencyOffer":"currencyOffer 3"}], "transactions":[
        {"transactionId":1,"typeTransaction":"typeTransaction 1","amount":500.50,"currencyTransaction":"currencyTransaction 1","policyNumber":"policyNumber 1"},
        {"transactionId":2,"typeTransaction":"typeTransaction 2","amount":550.50,"currencyTransaction":"currencyTransaction 2","policyNumber":"policyNumber 2"},
        {"transactionId":3,"typeTransaction":"typeTransaction 3","amount":600.50,"currencyTransaction":"currencyTransaction 3","policyNumber":"policyNumber 3"}
]}'
    recreateComposite "$INCP_ID_EMP_INSOFF_TRAN" "$body"
}

set -e

echo "Start:" `date`

echo "HOST=${HOST}"
echo "PORT=${PORT}"

if [[ $@ == *"start"* ]]
then
    echo "Restarting the test environment..."
    echo "$ docker-compose down --remove-orphans"
    docker-compose down --remove-orphans
    echo "$ docker-compose up -d"
    docker-compose up -d
fi

waitForService curl http://$HOST:$PORT/actuator/health

setupTestdata

waitForMessageProcessing

# Verify that a normal request works
assertCurl 200 "curl http://$HOST:$PORT/insurance-company-composite/$INCP_ID_EMP_INSOFF_TRAN -s"
assertEqual 1 $(echo $RESPONSE | jq .insuranceCompanyId)
assertEqual 3 $(echo $RESPONSE | jq ".employees | length")
assertEqual 3 $(echo $RESPONSE | jq ".insuranceOffers | length")
assertEqual 3 $(echo $RESPONSE | jq ".transactions | length")


# Verify that a 404 (Not Found) error is returned for a non existing insuranceCompanyId
assertCurl 404 "curl http://$HOST:$PORT/insurance-company-composite/"$INCP_ID_NOT_FOUND" -s"

# Verify that no employees are returned for insuranceCompanyId
assertCurl 200 "curl http://$HOST:$PORT/insurance-company-composite/$INCP_ID_NO_EMP -s"
assertEqual 113 $(echo $RESPONSE | jq .insuranceCompanyId)
assertEqual 0 $(echo $RESPONSE | jq ".employees | length")
assertEqual 3 $(echo $RESPONSE | jq ".insuranceOffers | length")
assertEqual 3 $(echo $RESPONSE | jq ".transactions | length")

# Verify that no insuranceOffers are returned for insuranceCompanyId
assertCurl 200 "curl http://$HOST:$PORT/insurance-company-composite/$INCP_ID_NO_INSOFF -s"
assertEqual 213 $(echo $RESPONSE | jq .insuranceCompanyId)
assertEqual 3 $(echo $RESPONSE | jq ".employees | length")
assertEqual 0 $(echo $RESPONSE | jq ".insuranceOffers | length")
assertEqual 3 $(echo $RESPONSE | jq ".transactions | length")

# Verify that no transactions are returned for insuranceCompanyId
assertCurl 200 "curl http://$HOST:$PORT/insurance-company-composite/$INCP_ID_NO_TRAN -s"
assertEqual 313 $(echo $RESPONSE | jq .insuranceCompanyId)
assertEqual 3 $(echo $RESPONSE | jq ".employees | length")
assertEqual 3 $(echo $RESPONSE | jq ".insuranceOffers | length")
assertEqual 0 $(echo $RESPONSE | jq ".transactions | length")

# Verify that a 422 (Unprocessable Entity) error is returned for a insuranceCompanyId that is out of range (-1)
assertCurl 422 "curl http://$HOST:$PORT/insurance-company-composite/-1 -s"
assertEqual "\"Invalid insuranceCompanyId: -1\"" "$(echo $RESPONSE | jq .message)"

# Verify that a 400 (Bad Request) error error is returned for a insuranceCompanyId that is not a number, i.e. invalid format
assertCurl 400 "curl http://$HOST:$PORT/insurance-company-composite/invalidInsuranceCompanyId -s"
assertEqual "\"Type mismatch.\"" "$(echo $RESPONSE | jq .message)"

echo "End:" `date`

if [[ $@ == *"stop"* ]]
then
    echo "We are done, stopping the test environment..."
    echo "$ docker-compose down --remove-orphans"
    docker-compose down --remove-orphans
fi



