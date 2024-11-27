import { render, screen } from "@testing-library/react";
import recommendationRequestFixtures from "fixtures/recommendationRequestFixtures";
import RequestsTable from "main/components/Requests/RequestsTable";

describe("RequestsTable tests", () => {
  test("renders without crashing for empty table", () => {
    render(<RequestsTable requests={[]} />);
  });

  test("renders without crashing for three requests", () => {
    render(
      <RequestsTable
        requests={recommendationRequestFixtures.oneRecommendationRequest}
      />,
    );
  });

  test("Has the expected column headers and content", () => {
    render(
      <RequestsTable
        requests={recommendationRequestFixtures.threeRecommendationRequests}
      />,
    );

    const expectedHeaders = [
      "id",
      "Requester Id",
      "Professor Id",
      "Request Type",
      "Details",
      "Needed By Date",
      "Submission Date",
      "Completion Date",
      "Status",
    ];
    const expectedFields = [
      "id",
      "requesterId",
      "professorId",
      "requestType",
      "details",
      "neededByDate",
      "submissionDate",
      "completionDate",
      "status",
    ];
    const testId = "RequestsTable";

    expectedHeaders.forEach((headerText) => {
      const header = screen.getByText(headerText);
      expect(header).toBeInTheDocument();
    });

    expectedFields.forEach((field) => {
      const header = screen.getByTestId(`${testId}-cell-row-0-col-${field}`);
      expect(header).toBeInTheDocument();
    });

    expect(screen.getByTestId(`${testId}-cell-row-0-col-id`)).toHaveTextContent(
      "1",
    );
    expect(
      screen.getByTestId(`${testId}-cell-row-0-col-requesterId`),
    ).toHaveTextContent("true");
    expect(screen.getByTestId(`${testId}-cell-row-1-col-id`)).toHaveTextContent(
      "2",
    );
    expect(
      screen.getByTestId(`${testId}-cell-row-1-col-admin`),
    ).toHaveTextContent("true");
  });
});
