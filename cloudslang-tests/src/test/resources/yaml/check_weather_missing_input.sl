#   (c) Copyright 2014 Hewlett-Packard Development Company, L.P.
#   All rights reserved. This program and the accompanying materials
#   are made available under the terms of the Apache License v2.0 which accompany this distribution.
#
#   The Apache License is available at
#   http://www.apache.org/licenses/LICENSE-2.0

namespace: user.ops

operation:
  name: check_weather_missing_input
  inputs:
    - city: "city"
    - input_get_missing_input:
            default: ${missing_input}
            required: false
            private: true
  python_action:
    script: |
      weather = "weather thing"
      print city
  outputs:
    - weather: ${ weather }
  results:
    - SUCCESS: ${ weather == "weather thing" }
    - FAILURE
