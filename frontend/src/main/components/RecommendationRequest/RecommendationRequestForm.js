import { Button, Form, Row, Col } from "react-bootstrap";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";

function RecommendationRequestForm({
  initialContents,
  submitAction,
  buttonLabel = "Create",
}) {
  // Stryker disable all
  const {
    register,
    formState: { errors },
    handleSubmit,
  } = useForm({ defaultValues: initialContents || {} });
  // Stryker restore all

  const navigate = useNavigate();

  // For explanation, see: https://stackoverflow.com/questions/3143070/javascript-regex-iso-datetime
  // Note that even this complex regex may still need some tweaks

  // Stryker disable Regex
  // const isodate_regex =
  //   /(\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d:[0-5]\d\.\d+)|(\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d:[0-5]\d)|(\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d)/i;
  // Stryker restore Regex

  // Stryker disable next-line all
  // const yyyyq_regex = /((19)|(20))\d{2}[1-4]/i; // Accepts from 1900-2099 followed by 1-4.  Close enough.

  return (
    <Form onSubmit={handleSubmit(submitAction)}>
      <Row>
        {initialContents && (
          <Col>
            <Form.Group className="mb-3">
              <Form.Label htmlFor="id">Id</Form.Label>
              <Form.Control
                data-testid="RecommendationRequestForm-id"
                id="id"
                type="text"
                {...register("id")}
                value={initialContents.id}
                disabled
              />
            </Form.Group>
          </Col>
        )}

        {/* Requester Email */}
        <Col>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="requesterEmail">Requester Email</Form.Label>
            <Form.Control
              data-testid="RecommendationRequestForm-requesterEmail"
              id="requesterEmail"
              type="email"
              isInvalid={Boolean(errors.requesterEmail)}
              {...register("requesterEmail", {
                required: true,
              })}
            />
            <Form.Control.Feedback type="invalid">
              {errors.requesterEmail && "Requester Email is required. "}
            </Form.Control.Feedback>
          </Form.Group>
        </Col>

        {/* Requester Name */} 
        <Col>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="requesterName">Requester Name</Form.Label>
            <Form.Control
              data-testid="RecommendationRequestForm-requesterName"
              id="requesterName"
              type="text"
              isInvalid={Boolean(errors.requesterName)}
              {...register("requesterName", {
                required: true,
              })}
            />
            <Form.Control.Feedback type="invalid">
              {errors.requesterName && "Requester Name is required. "}
            </Form.Control.Feedback>
          </Form.Group>
        </Col>

        {/* Professor Email */} 
        <Col>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="professorEmail">Professor Email</Form.Label>
            <Form.Control
              data-testid="RecommendationRequestForm-professorEmail"
              id="professorEmail"
              type="email"
              isInvalid={Boolean(errors.professorEmail)}
              {...register("professorEmail", {
                required: true,
              })}
            />
            <Form.Control.Feedback type="invalid">
              {errors.professorEmail && "Professor Email is required. "}
            </Form.Control.Feedback>
          </Form.Group>
        </Col>

        {/* Professor Name */} 
        <Col>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="professorName">Professor Name</Form.Label>
            <Form.Control
              data-testid="RecommendationRequestForm-professorName"
              id="professorName"
              type="text"
              isInvalid={Boolean(errors.professorName)}
              {...register("professorName", {
                required: true,
              })}
            />
            <Form.Control.Feedback type="invalid">
              {errors.professorName && "Professor Name is required. "}
            </Form.Control.Feedback>
          </Form.Group>
        </Col>
      </Row>

      <Row>
        {/* Request Type */}
        <Col>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="requestType">Request Type</Form.Label>
            <Form.Select
              data-testid="RecommendationRequestForm-requestType"
              id="requestType"
              type="string"
              isInvalid={Boolean(errors.requestType)}
              {...register("requestType", {
                required: "Request Type is required.",
              })}
            >
              <option value="">-- Select an Option --</option> 
              <option value="CS Department BS/MS program">CS Department BS/MS program</option>
              <option value="Scholarship or Fellowship">Scholarship or Fellowship</option>
              <option value="MS program (other than CS Dept BS/MS)">
                MS program (other than CS Dept BS/MS)
              </option>
              <option value="PhD program">PhD program</option>
              <option value="Other">Other</option>
            </Form.Select>
            <Form.Control.Feedback type="invalid">
              {errors.requestType?.message}
            </Form.Control.Feedback>
          </Form.Group>
        </Col>


        {/* Details */}
        <Col>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="details">Details</Form.Label>
            <Form.Control
              data-testid="RecommendationRequestForm-details"
              id="details"
              type="text"
              placeholder="Enter additional details (optional)"
              isInvalid={Boolean(errors.details)}
              {...register("details")}
            />
          </Form.Group>
        </Col>
      </Row>

      {/* Submission Date */}
      <Row>
        <Col>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="submissionDate">Submission Date</Form.Label>
            <Form.Control
              data-testid="RecommendationRequestForm-submissionDate"
              id="submissionDate"
              type="datetime-local"
              isInvalid={Boolean(errors.submissionDate)}
              {...register("submissionDate", {
                required: "Submission Date is required.",
              })}
            />
            <Form.Control.Feedback type="invalid">
              {errors.submissionDate?.message}
            </Form.Control.Feedback>
          </Form.Group>
        </Col>

        {/* Completion Date */}
        <Col>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="completionDate">Completion Date</Form.Label>
            <Form.Control
              data-testid="RecommendationRequestForm-completionDate"
              id="completionDate"
              type="datetime-local"
              isInvalid={Boolean(errors.completionDate)}
              {...register("completionDate")}
            />
          </Form.Group>
        </Col>
      </Row>

      <Row>
        <Col>
          <Button type="submit" data-testid="RecommendationRequestForm-submit">
            {buttonLabel}
          </Button>
          <Button
            variant="Secondary"
            onClick={() => navigate(-1)}
            data-testid="RecommendationRequestForm-cancel"
          >
            Cancel
          </Button>
        </Col>
      </Row>
    </Form>
  );
}

export default RecommendationRequestForm;
