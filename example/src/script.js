import { CapCallKeep } from "capacitor-callkeep"

CapCallKeep.setup({})
async function foo() {
    const result = await CapCallKeep.echo({ value: "bob" })
    console.log("echo result is:", JSON.stringify(result))

    const callResult = CapCallKeep.displayIncomingCall("sdfsdf", "aaaa",)
    console.log(JSON.stringify({ callResult }))

}
foo()