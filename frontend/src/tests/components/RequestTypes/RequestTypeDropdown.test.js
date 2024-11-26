import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import "@testing-library/jest-dom/extend-expect";
import { useState } from "react";

import RequestTypeDropdown from "main/components/RequestTypes/RequestTypeDropdown";
import { requestTypeFixtures } from "fixtures/requestTypeFixtures";

jest.mock("react", () => ({
  ...jest.requireActual("react"),
  useState: jest.fn(),
}));

describe("RequestTypeDropdown tests", () => {
  beforeEach(() => {
    jest.spyOn(console, "error");
    console.error.mockImplementation(() => null);
  });

  beforeEach(() => {
    useState.mockImplementation(jest.requireActual("react").useState);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  afterEach(() => {
    console.error.mockRestore();
  });

  const requestType = jest.fn();
  const setRequestType = jest.fn();

  test("renders without crashing on one request type", () => {
    render(
      <RequestTypeDropdown
        requestTypes={requestTypeFixtures.oneRequestType}
        requestType={requestTypeFixtures.oneRequestType}
        setRequestType={setRequestType}
        controlId="ssd1"
      />,
    );
  });

  test("renders without crashing on three request types", async () => {
    render(
      <RequestTypeDropdown
        requestTypes={requestTypeFixtures.threeRequestTypes}
        requestType={requestType}
        setRequestType={setRequestType}
        controlId="ssd1"
      />,
    );

    // Check that blanks are replaced with hyphens
    await waitFor(() =>
      expect(screen.getByTestId("ssd1-option-2").toBeInTheDocument),
    );
    await waitFor(() =>
      expect(screen.getByTestId("ssd1-option-3").toBeInTheDocument),
    );
    await waitFor(() =>
      expect(screen.getByTestId("ssd1-option-4").toBeInTheDocument),
    );
  });

  test("when I select an object, the value changes", async () => {
    render(
      <RequestTypeDropdown
        requestTypes={requestTypeFixtures.threeRequestTypes}
        requestType={requestType}
        setRequestType={setRequestType}
        controlId="ssd1"
      />,
    );

    expect(await screen.findByLabelText("Request Type")).toBeInTheDocument();

    const selectQuarter = screen.getByLabelText("Request Type");
    userEvent.selectOptions(selectQuarter, "Other");
    expect(setRequestType).toBeCalledWith("Other");
  });

  test("if I pass a non-null onChange, it gets called when the value changes", async () => {
    const onChange = jest.fn();
    render(
      <RequestTypeDropdown
        requestTypes={requestTypeFixtures.threeRequestTypes}
        requestType={requestType}
        setRequestType={setRequestType}
        controlId="ssd1"
        onChange={onChange}
      />,
    );

    expect(await screen.findByLabelText("Request Type")).toBeInTheDocument();

    const selectSubject = screen.getByLabelText("Request Type");
    userEvent.selectOptions(selectSubject, "Other");
    await waitFor(() => expect(setRequestType).toBeCalledWith("Other"));
    await waitFor(() => expect(onChange).toBeCalledTimes(1));

    // x.mock.calls[0][0] is the first argument of the first call to the jest.fn() mock x
    const event = onChange.mock.calls[0][0];
    expect(event.target.value).toBe("Other");
  });

  test("default label is Subject Area", async () => {
    render(
      <RequestTypeDropdown
        requestTypes={requestTypeFixtures.threeRequestTypes}
        requestType={requestType}
        setRequestType={setRequestType}
        controlId="ssd1"
      />,
    );

    expect(await screen.findByLabelText("Request Type")).toBeInTheDocument();
  });

  test("keys / testids are set correctly on options", async () => {
    render(
      <RequestTypeDropdown
        requestTypes={requestTypeFixtures.threeRequestTypes}
        requestType={requestType}
        setRequestType={setRequestType}
        controlId="ssd1"
      />,
    );

    const expectedKey = "ssd1-option-3";
    await waitFor(() =>
      expect(screen.getByTestId(expectedKey).toBeInTheDocument),
    );
  });

  test("when localstorage has a value, it is passed to useState", async () => {
    const getItemSpy = jest.spyOn(Storage.prototype, "getItem");
    getItemSpy.mockImplementation(() => "Other");

    const setRequestTypeStateSpy = jest.fn();
    useState.mockImplementation((x) => [x, setRequestTypeStateSpy]);

    render(
      <RequestTypeDropdown
        requestTypes={requestTypeFixtures.threeRequestTypes}
        requestType={requestType}
        setRequestType={setRequestType}
        controlId="ssd1"
      />,
    );

    await waitFor(() => expect(useState).toBeCalledWith("Other"));
  });

  test("when localstorage has no value, first element of request type list is passed to useState", async () => {
    const getItemSpy = jest.spyOn(Storage.prototype, "getItem");
    getItemSpy.mockImplementation(() => null);

    const setRequestTypeStateSpy = jest.fn();
    useState.mockImplementation((x) => [x, setRequestTypeStateSpy]);

    render(
      <RequestTypeDropdown
        requestTypes={requestTypeFixtures.threeRequestTypes}
        requestType={requestType}
        setRequestType={setRequestType}
        controlId="ssd1"
      />,
    );

    await waitFor(() =>
      expect(useState).toBeCalledWith(expect.objectContaining({})),
    );
  });

  test("When no subjects, dropdown is blank", async () => {
    render(
      <RequestTypeDropdown
        requestTypes={[]}
        requestType={requestType}
        setRequestType={setRequestType}
        controlId="ssd1"
      />,
    );

    const expectedKey = "ssd1";
    expect(screen.queryByTestId(expectedKey)).toBeNull();
  });
});
