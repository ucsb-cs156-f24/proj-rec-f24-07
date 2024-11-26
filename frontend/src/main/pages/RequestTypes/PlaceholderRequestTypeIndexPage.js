import BasicLayout from "main/layouts/BasicLayout/BasicLayout";

export default function PlaceholderRequestTypeIndexPage() {
  // Stryker disable all : placeholder for future implementation
  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>Index page not yet implemented</h1>
        <p>
          <a href="/admin/requesttypes/create">Create</a>
        </p>
        <p>
          <a href="/admin/requesttypes/edit/1">Edit (not yet implemented)</a>
        </p>
      </div>
    </BasicLayout>
  );
}
