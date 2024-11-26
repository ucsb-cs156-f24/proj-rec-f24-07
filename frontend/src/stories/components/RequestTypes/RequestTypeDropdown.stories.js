import React, { useState } from "react";

import RequestTypeDropdown from "main/components/RequestTypes/RequestTypeDropdown";
import { requestTypeFixtures } from "fixtures/requestTypeFixtures";

export default {
  title: "components/RequestTypes/RequestTypeDropdown",
  component: RequestTypeDropdown,
};

const Template = (args) => {
  const [requestType, setRequestType] = useState(args.requestTypes[0]);

  return (
    <RequestTypeDropdown
      requestType={requestType}
      setRequestType={setRequestType}
      controlId={"RequestTypeId"}
      label={"Request Type"}
      {...args}
    />
  );
};

export const OneRequestType = Template.bind({});
OneRequestType.args = {
  requestTypes: requestTypeFixtures.oneRequestType,
};

export const ThreeRequestTypes = Template.bind({});
ThreeRequestTypes.args = {
  requestTypes: requestTypeFixtures.threeRequestTypes,
};
