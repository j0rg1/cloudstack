# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

from marvin.cloudstackTestCase import *
from marvin.cloudstackAPI import *
from marvin.integration.lib.utils import *
from marvin.integration.lib.base import *
from marvin.integration.lib.common import *
from nose.plugins.attrib import attr

class Services:
    def __init__(self):
        self.services = {
            "region": {
                "regionid": "2",
                "regionname": "Region2",
                "regionendpoint": "http://region2:8080/client"
            }
        }


class TestRegions(cloudstackTestCase):
    """Test Regions - basic region creation
    """

    @classmethod
    def setUpClass(cls):
        cls.api_client = super(TestRegions, cls).getClsTestClient().getApiClient()
        cls.services = Services().services
        cls.domain = get_domain(cls.api_client, cls.services)
        cls.cleanup = []

    @attr(tags=["simulator", "basic", "advanced", "selfservice"])
    def test_createRegion(self):
        """ Test for create region
        """
        region = Region.create(self.api_client,
            self.services["region"]
        )

        list_region = Region.list(self.api_client,
            id=self.services["region"]["regionid"]
        )

        self.assertEqual(
            isinstance(list_region, list),
            True,
            "Check for list Region response"
        )
        region_response = list_region[0]

        self.assertEqual(
            str(region_response.id),
            self.services["region"]["regionid"],
            "listRegion response does not match with region Id created"
        )

        self.assertEqual(
            region_response.name,
            self.services["region"]["regionname"],
            "listRegion response does not match with region name created"
        )
        self.assertEqual(
            region_response.endpoint,
            self.services["region"]["regionendpoint"],
            "listRegion response does not match with region endpoint created"
        )
        self.cleanup.append(region)
        return

    @classmethod
    def tearDownClass(cls):
        try:
            #Clean up
            cleanup_resources(cls.api_client, cls.cleanup)
            list_region = Region.list(cls.api_client, id=cls.services["region"]["regionid"])
            assert list_region is None, "Region deletion fails"
        except Exception as e:
            raise Exception("Warning: Region cleanup/delete fails with : %s" % e)