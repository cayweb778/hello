export function useRegiterEvent({ emit, data, actions }) {
  const event = {
    ...data,
    ...actions,
  };
  emit('register', event);
  return event;
}

export function useRegiterEventNoEmit({ data, actions }) {
  const event = {
    ...data,
    ...actions,
  };
  return event;
}
