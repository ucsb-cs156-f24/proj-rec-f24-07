package edu.ucsb.cs156.rec.errors;

/**
 * This is an error class for a custom RuntimeException in Java that is used to indicate
 * when an entity of a specific type with a given ID already exists in the repository.
 */
public class EntityAlreadyExistsException extends RuntimeException {
  /**
   * Constructor for the exception
   * 
   * @param entityType The class of the entity that already existed, e.g. User.class
   * @param type the type that was trying to be saved
   */
  public EntityAlreadyExistsException(Class<?> entityType, Object type) {
    super("%s %s already exists"
      .formatted(entityType.getSimpleName(), type.toString()));
  }
}