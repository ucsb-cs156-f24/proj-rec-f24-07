import { render, waitFor, fireEvent, screen } from "@testing-library/react";
import RecommendationRequestForm from "main/components/RecommendationRequest/RecommendationRequestForm";
import { recommendationRequestFixtures } from "fixtures/recommendationRequestFixtures";
import { BrowserRouter as Router } from "react-router-dom";

const mockedNavigate = jest.fn();

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockedNavigate,
}));

describe("RecommendationRequestForm tests", () => {
  test("renders correctly", async () => {
    render(
      <Router>
        <RecommendationRequestForm />
      </Router>,
    );

    // Check for form fields
    await screen.findByText(/Requester Email/);
    await screen.findByText(/Requester Name/);
    await screen.findByText(/Professor Email/);
    await screen.findByText(/Professor Name/);
    await screen.findByText(/Request Type/);
    await screen.findByText(/Details/);
    await screen.findByText(/Submission Date/);
    await screen.findByText(/Completion Date/);
    await screen.findByText(/Create/);
  });

  test("renders correctly when passing in a Recommendation Request", async () => {
    render(
      <Router>
        <RecommendationRequestForm
          initialContents={
            recommendationRequestFixtures.oneRecommendationRequest[0]
          }
        />
      </Router>,
    );

    await screen.findByTestId("RecommendationRequestForm-id");
    expect(screen.getByText(/Id/)).toBeInTheDocument();
    expect(screen.getByTestId("RecommendationRequestForm-id")).toHaveValue("1");
  });

  test("Correct Error messages on bad input", async () => {
    render(
      <Router>
        <RecommendationRequestForm />
      </Router>,
    );

    const requesterEmailField = screen.getByTestId(
      "RecommendationRequestForm-requesterEmail",
    );
    const submitButton = screen.getByTestId("RecommendationRequestForm-submit");
    fireEvent.change(requesterEmailField, { target: { value: "" } });
    fireEvent.click(submitButton);

    expect(
      await screen.findByText("Requester Email is required."),
    ).toBeInTheDocument();
    expect(
      await screen.findByText("Professor Email is required."),
    ).toBeInTheDocument();
    expect(
      await screen.findByText("Requester Name is required."),
    ).toBeInTheDocument();
    expect(
      await screen.findByText("Professor Name is required."),
    ).toBeInTheDocument();
    expect(
      await screen.findByText("Request Type is required."),
    ).toBeInTheDocument();
    expect(
      await screen.findByText("Submission Date is required."),
    ).toBeInTheDocument();
  });

  test("No Error messages on good input", async () => {
    const mockSubmitAction = jest.fn();

    render(
      <Router>
        <RecommendationRequestForm submitAction={mockSubmitAction} />
      </Router>,
    );

    await screen.findByTestId("RecommendationRequestForm-requesterEmail");
    const requesterEmailField = screen.getByTestId(
      "RecommendationRequestForm-requesterEmail",
    );
    const requesterNameField = screen.getByTestId(
      "RecommendationRequestForm-requesterName",
    );
    const professorEmailField = screen.getByTestId(
      "RecommendationRequestForm-professorEmail",
    );
    const professorNameField = screen.getByTestId(
      "RecommendationRequestForm-professorName",
    );
    const requestTypeField = screen.getByTestId(
      "RecommendationRequestForm-requestType",
    );
    const detailsField = screen.getByTestId(
      "RecommendationRequestForm-details",
    );
    const submissionDateField = screen.getByTestId(
      "RecommendationRequestForm-submissionDate",
    );
    const completionDateField = screen.getByTestId(
      "RecommendationRequestForm-completionDate",
    );
    const submitButton = screen.getByTestId("RecommendationRequestForm-submit");

    fireEvent.change(requesterEmailField, {
      target: { value: "test@example.com" },
    });
    fireEvent.change(requesterNameField, { target: { value: "Test User" } });
    fireEvent.change(professorEmailField, {
      target: { value: "prof@example.com" },
    });
    fireEvent.change(professorNameField, {
      target: { value: "Professor Name" },
    });
    fireEvent.change(requestTypeField, { target: { value: "Other" } });
    fireEvent.change(detailsField, { target: { value: "Some details" } });
    fireEvent.change(submissionDateField, {
      target: { value: "2022-04-20T12:00" },
    });
    fireEvent.change(completionDateField, {
      target: { value: "2022-05-20T12:00" },
    });

    fireEvent.click(submitButton);

    await waitFor(() => expect(mockSubmitAction).toHaveBeenCalled());
  });

  test("that navigate(-1) is called when Cancel is clicked", async () => {
    render(
      <Router>
        <RecommendationRequestForm />
      </Router>,
    );

    await screen.findByTestId("RecommendationRequestForm-cancel");
    const cancelButton = screen.getByTestId("RecommendationRequestForm-cancel");
    fireEvent.click(cancelButton);

    await waitFor(() => expect(mockedNavigate).toHaveBeenCalledWith(-1));
  });
});
