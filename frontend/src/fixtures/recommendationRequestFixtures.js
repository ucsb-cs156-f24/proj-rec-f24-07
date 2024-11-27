const recommendationRequestFixtures = {
  oneRecommendationRequest: [
    {
      id: 1,

      requesterId: 6,

      professorId: 1,

      requestType: "CS Department BS/MS program",
      details: "Extension deadline request :)",

      neededByDate: "2022-04-02T12:00",
      submissionDate: "2023-06-01T02:00:00",
      completionDate: null,

      status: "Completed",
    },
  ],

  threeRecommendationRequests: [
    {
      id: 2,

      requesterId: 7,

      professorId: 2,

      requestType: "Scholarship or Fellowship",
      details: "scholarship details",

      neededByDate: "2022-09-02T12:00",
      submissionDate: "2023-08-01T02:00:00",
      completionDate: null,

      status: "Pending",
    },
    {
      id: 3,

      requesterId: 9,

      professorId: 1,

      requestType: "PhD program",
      details: "PHD deadline details",

      neededByDate: "2022-17-02T12:00",
      submissionDate: "2023-04-01T02:00:00",
      completionDate: null,

      status: "Rejected",
    },
    {
      id: 4,

      requesterId: 10,

      professorId: 2,

      requestType: "Other",
      details: "Regrade final exam",

      neededByDate: "2022-27-02T12:00",
      submissionDate: "2023-09-01T02:00:00",
      completionDate: null,

      status: "Pending",
    },
  ],
};

export { recommendationRequestFixtures };
