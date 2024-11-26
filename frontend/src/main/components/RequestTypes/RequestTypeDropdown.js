import React, { useState } from "react";
import { Form } from "react-bootstrap";

const RequestTypeDropdown = ({
  requestTypes,
  requestType,
  setRequestType,
  controlId,
  onChange = null,
  label = "Request Type",
}) => {
  const localSearchType = localStorage.getItem(controlId);

  const [requestTypeState, setRequestTypeState] = useState(
    // Stryker disable next-line all : not sure how to test/mock local storage
    localSearchType || requestType,
  );

  const handleSubjectOnChange = (event) => {
    localStorage.setItem(controlId, event.target.value);
    setRequestTypeState(event.target.value);
    setRequestType(event.target.value);
    if (onChange != null) {
      onChange(event);
    }
  };

  return (
    <Form.Group controlId={controlId}>
      <Form.Label>{label}</Form.Label>
      <Form.Control
        as="select"
        value={requestTypeState}
        onChange={handleSubjectOnChange}
      >
        {requestTypes.map(function (object) {
          const id = object.id;
          const key = `${controlId}-option-${id}`;
          return (
            <option key={key} data-testid={key} value={object.requestType}>
              {object.requestType}
            </option>
          );
        })}
      </Form.Control>
    </Form.Group>
  );
};

export default RequestTypeDropdown;
