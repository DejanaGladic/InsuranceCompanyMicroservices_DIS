# Sample usage:
#
#   HOST=localhost PORT=7000 ./test-em-all.bash
#
: ${HOST=localhost}
: ${PORT=8081}

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

function recreateComposite() {
    local insuranceCompanyId=$1
    local composite=$2

    assertCurl 200 "curl -X DELETE http://$HOST:$PORT/insurance-company-composite/${insuranceCompanyId} -s"
    curl -X POST http://$HOST:$PORT/insurance-company-composite -H "Content-Type: application/json" --data "$composite"
}

function setupTestdata() {

    body=\
'{"insuranceCompanyId":1,"name":"insurance company 1","city":"city 1","address":"address 1", "phoneNumber":"phoneNumber 1", "employees":[
        {"employeeId":1,"name":"name 1","surname":"surname 1","specialization":"specialization 1"},
        {"employeeId":2,"name":"name 2","surname":"surname 2","specialization":"specialization 2"},
        {"employeeId":3,"name":"name 3","surname":"surname 3","specialization":"specialization 3"}
    ], "insuranceOffers":[
        {"insuranceOfferId":1,"offerName":"offerName 1","price":500.50,"currencyOffer":"currencyOffer 1"},
        {"insuranceOfferId":2,"offerName":"offerName 2","price":550.50,"currencyOffer":"currencyOffer 2"},
        {"insuranceOfferId":3,"offerName":"offerName 3","price":600.50,"currencyOffer":"currencyOffer 3"}
    ], "transactions":[
        {"transactionId":1,"typeTransaction":"typeTransaction 1","amount":500.50,"currencyTransaction":"currencyTransaction 1","policyNumber":"policyNumber 1"},
        {"transactionId":2,"typeTransaction":"typeTransaction 2","amount":550.50,"currencyTransaction":"currencyTransaction 2","policyNumber":"policyNumber 2"},
        {"transactionId":3,"typeTransaction":"typeTransaction 3","amount":600.50,"currencyTransaction":"currencyTransaction 3","policyNumber":"policyNumber 3"}
    ]}'
    recreateComposite 1 "$body"

    body=\
'{"insuranceCompanyId":1,"name":"insurance company 1","city":"city 1","address":"address 1", "phoneNumber":"phoneNumber 1", "insuranceOffers":[
        {"insuranceOfferId":1,"offerName":"offerName 1","price":500.50,"currencyOffer":"currencyOffer 1"},
        {"insuranceOfferId":2,"offerName":"offerName 2","price":550.50,"currencyOffer":"currencyOffer 2"},
        {"insuranceOfferId":3,"offerName":"offerName 3","price":600.50,"currencyOffer":"currencyOffer 3"}
    ], "transactions":[
        {"transactionId":1,"typeTransaction":"typeTransaction 1","amount":500.50,"currencyTransaction":"currencyTransaction 1","policyNumber":"policyNumber 1"},
        {"transactionId":2,"typeTransaction":"typeTransaction 2","amount":550.50,"currencyTransaction":"currencyTransaction 2","policyNumber":"policyNumber 2"},
        {"transactionId":3,"typeTransaction":"typeTransaction 3","amount":600.50,"currencyTransaction":"currencyTransaction 3","policyNumber":"policyNumber 3"}
    ]}'
    recreateComposite 113 "$body"

    body=\
'{"insuranceCompanyId":1,"name":"insurance company 1","city":"city 1","address":"address 1", "phoneNumber":"phoneNumber 1", "employees":[
        {"employeeId":1,"name":"name 1","surname":"surname 1","specialization":"specialization 1"},
        {"employeeId":2,"name":"name 2","surname":"surname 2","specialization":"specialization 2"},
        {"employeeId":3,"name":"name 3","surname":"surname 3","specialization":"specialization 3"}
    ], "transactions":[
        {"transactionId":1,"typeTransaction":"typeTransaction 1","amount":500.50,"currencyTransaction":"currencyTransaction 1","policyNumber":"policyNumber 1"},
        {"transactionId":2,"typeTransaction":"typeTransaction 2","amount":550.50,"currencyTransaction":"currencyTransaction 2","policyNumber":"policyNumber 2"},
        {"transactionId":3,"typeTransaction":"typeTransaction 3","amount":600.50,"currencyTransaction":"currencyTransaction 3","policyNumber":"policyNumber 3"}
    ]}'
    recreateComposite 213 "$body"
	
    body=\
'{"insuranceCompanyId":1,"name":"insurance company 1","city":"city 1","address":"address 1", "phoneNumber":"phoneNumber 1", "employees":[
        {"employeeId":1,"name":"name 1","surname":"surname 1","specialization":"specialization 1"},
        {"employeeId":2,"name":"name 2","surname":"surname 2","specialization":"specialization 2"},
        {"employeeId":3,"name":"name 3","surname":"surname 3","specialization":"specialization 3"}
    ], "insuranceOffers":[
        {"insuranceOfferId":1,"offerName":"offerName 1","price":500.50,"currencyOffer":"currencyOffer 1"},
        {"insuranceOfferId":2,"offerName":"offerName 2","price":550.50,"currencyOffer":"currencyOffer 2"},
        {"insuranceOfferId":3,"offerName":"offerName 3","price":600.50,"currencyOffer":"currencyOffer 3"}
    ]}'
    recreateComposite 313 "$body"

}

set -e

echo "Start:" `date`

echo "HOST=${HOST}"
echo "PORT=${PORT}"

if [[ $@ == *"start"* ]]
then
    echo "Restarting the test environment..."
    echo "$ docker-compose down"
    docker-compose down
    echo "$ docker-compose up -d"
    docker-compose up -d
fi

waitForService curl -X DELETE http://$HOST:$PORT/insurance-company-composite/13

setupTestdata

# Verify that a normal request works
assertCurl 200 "curl http://$HOST:$PORT/insurance-company-composite/1 -s"
assertEqual 1 $(echo $RESPONSE | jq .insuranceCompanyId)
assertEqual 3 $(echo $RESPONSE | jq ".employees | length")
assertEqual 3 $(echo $RESPONSE | jq ".insuranceOffers | length")
assertEqual 3 $(echo $RESPONSE | jq ".transactions | length")

# Verify that a 404 (Not Found) error is returned for a non existing insuranceCompanyId 13
assertCurl 404 "curl http://$HOST:$PORT/insurance-company-composite/13 -s"

# Verify that no employees are returned for insuranceCompanyId 113
assertCurl 200 "curl http://$HOST:$PORT/insurance-company-composite/113 -s"
assertEqual 113 $(echo $RESPONSE | jq .insuranceCompanyId)
assertEqual 0 $(echo $RESPONSE | jq ".employees | length")
assertEqual 3 $(echo $RESPONSE | jq ".insuranceOffers | length")
assertEqual 3 $(echo $RESPONSE | jq ".transactions | length")

# Verify that no insuranceOffers are returned for insuranceCompanyId 213
assertCurl 200 "curl http://$HOST:$PORT/insurance-company-composite/213 -s"
assertEqual 213 $(echo $RESPONSE | jq .insuranceCompanyId)
assertEqual 3 $(echo $RESPONSE | jq ".employees | length")
assertEqual 0 $(echo $RESPONSE | jq ".insuranceOffers | length")
assertEqual 3 $(echo $RESPONSE | jq ".transactions | length")

# Verify that no transactions are returned for insuranceCompanyId 313
assertCurl 200 "curl http://$HOST:$PORT/insurance-company-composite/313 -s"
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


if [[ $@ == *"stop"* ]]
then
    echo "We are done, stopping the test environment..."
    echo "$ docker-compose down"
    docker-compose down
fi

echo "End:" `date`

