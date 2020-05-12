public struct Option<T>
{
    public static Option<T> None => new Option<T>(default(T), false);
    public static Option<T> Some(T value) => new Option<T>(value, true);

    public readonly bool isSome;
    public readonly T value;

    Option(T value, bool isSome)
    {
        this.value = value;
        this.isSome = isSome;
    }
    
}
