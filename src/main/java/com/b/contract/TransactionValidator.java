package com.b.contract;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/hyperledger-web3j/web3j/tree/main/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.6.3.
 */
@SuppressWarnings("rawtypes")
public class TransactionValidator extends Contract {
    public static final String BINARY = "\"bytecode\": \"0x6080604052348015600f57600080fd5b50610a918061001f6000396000f3fe608060405234801561001057600080fd5b50600436106100365760003560e01c80633c64f04b1461003b5780637ba3dccf1461006e575b600080fd5b61005560048036038101906100509190610342565b61009e565b6040516100659493929190610418565b60405180910390f35b610088600480360381019061008391906105cc565b6101de565b6040516100959190610686565b60405180910390f35b60006020528060005260406000206000915090508060000180546100c1906106d0565b80601f01602080910402602001604051908101604052809291908181526020018280546100ed906106d0565b801561013a5780601f1061010f5761010080835404028352916020019161013a565b820191906000526020600020905b81548152906001019060200180831161011d57829003601f168201915b50505050509080600101805461014f906106d0565b80601f016020809104026020016040519081016040528092919081815260200182805461017b906106d0565b80156101c85780601f1061019d576101008083540402835291602001916101c8565b820191906000526020600020905b8154815290600101906020018083116101ab57829003601f168201915b5050505050908060020154908060030154905084565b6000806000808481526020019081526020016000206003015414610237576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161022e9061074d565b60405180910390fd5b604051806080016040528086815260200185815260200184815260200142815250600080848152602001908152602001600020600082015181600001908161027f9190610919565b5060208201518160010190816102959190610919565b5060408201518160020155606082015181600301559050507f45c98ac0b7d2aed30bd2a227b461b775233750bce2507405ab51156badce775385858542866040516102e49594939291906109fa565b60405180910390a160019050949350505050565b6000604051905090565b600080fd5b600080fd5b6000819050919050565b61031f8161030c565b811461032a57600080fd5b50565b60008135905061033c81610316565b92915050565b60006020828403121561035857610357610302565b5b60006103668482850161032d565b91505092915050565b600081519050919050565b600082825260208201905092915050565b60005b838110156103a957808201518184015260208101905061038e565b60008484015250505050565b6000601f19601f8301169050919050565b60006103d18261036f565b6103db818561037a565b93506103eb81856020860161038b565b6103f4816103b5565b840191505092915050565b6000819050919050565b610412816103ff565b82525050565b6000608082019050818103600083015261043281876103c6565b9050818103602083015261044681866103c6565b90506104556040830185610409565b6104626060830184610409565b95945050505050565b600080fd5b600080fd5b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b6104ad826103b5565b810181811067ffffffffffffffff821117156104cc576104cb610475565b5b80604052505050565b60006104df6102f8565b90506104eb82826104a4565b919050565b600067ffffffffffffffff82111561050b5761050a610475565b5b610514826103b5565b9050602081019050919050565b82818337600083830152505050565b600061054361053e846104f0565b6104d5565b90508281526020810184848401111561055f5761055e610470565b5b61056a848285610521565b509392505050565b600082601f8301126105875761058661046b565b5b8135610597848260208601610530565b91505092915050565b6105a9816103ff565b81146105b457600080fd5b50565b6000813590506105c6816105a0565b92915050565b600080600080608085870312156105e6576105e5610302565b5b600085013567ffffffffffffffff81111561060457610603610307565b5b61061087828801610572565b945050602085013567ffffffffffffffff81111561063157610630610307565b5b61063d87828801610572565b935050604061064e878288016105b7565b925050606061065f8782880161032d565b91505092959194509250565b60008115159050919050565b6106808161066b565b82525050565b600060208201905061069b6000830184610677565b92915050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b600060028204905060018216806106e857607f821691505b6020821081036106fb576106fa6106a1565b5b50919050565b7f5472616e7366657220616c726561647920657869737473000000000000000000600082015250565b600061073760178361037a565b915061074282610701565b602082019050919050565b600060208201905081810360008301526107668161072a565b9050919050565b60008190508160005260206000209050919050565b60006020601f8301049050919050565b600082821b905092915050565b6000600883026107cf7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff82610792565b6107d98683610792565b95508019841693508086168417925050509392505050565b6000819050919050565b600061081661081161080c846103ff565b6107f1565b6103ff565b9050919050565b6000819050919050565b610830836107fb565b61084461083c8261081d565b84845461079f565b825550505050565b600090565b61085961084c565b610864818484610827565b505050565b5b818110156108885761087d600082610851565b60018101905061086a565b5050565b601f8211156108cd5761089e8161076d565b6108a784610782565b810160208510156108b6578190505b6108ca6108c285610782565b830182610869565b50505b505050565b600082821c905092915050565b60006108f0600019846008026108d2565b1980831691505092915050565b600061090983836108df565b9150826002028217905092915050565b6109228261036f565b67ffffffffffffffff81111561093b5761093a610475565b5b61094582546106d0565b61095082828561088c565b600060209050601f8311600181146109835760008415610971578287015190505b61097b85826108fd565b8655506109e3565b601f1984166109918661076d565b60005b828110156109b957848901518255600182019150602085019450602081019050610994565b868310156109d657848901516109d2601f8916826108df565b8355505b6001600288020188555050505b505050505050565b6109f48161030c565b82525050565b600060a0820190508181036000830152610a1481886103c6565b90508181036020830152610a2881876103c6565b9050610a376040830186610409565b610a446060830185610409565b610a5160808301846109eb565b969550505050505056fea2646970667358221220a04360770ace7ce25e553deb58030b74626a4907d99fccc69321a494b693fd5f64736f6c634300081c0033\"";

    private static String librariesLinkedBinary;

    public static final String FUNC_TRANSFERS = "transfers";

    public static final String FUNC_VALIDATETRANSFER = "validateTransfer";

    public static final Event TRANSFERVALIDATED_EVENT = new Event("TransferValidated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bytes32>() {}));
    ;

    @Deprecated
    protected TransactionValidator(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected TransactionValidator(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected TransactionValidator(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected TransactionValidator(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<TransferValidatedEventResponse> getTransferValidatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(TRANSFERVALIDATED_EVENT, transactionReceipt);
        ArrayList<TransferValidatedEventResponse> responses = new ArrayList<TransferValidatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferValidatedEventResponse typedResponse = new TransferValidatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sourceCardId = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.destCardId = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.txHash = (byte[]) eventValues.getNonIndexedValues().get(4).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static TransferValidatedEventResponse getTransferValidatedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(TRANSFERVALIDATED_EVENT, log);
        TransferValidatedEventResponse typedResponse = new TransferValidatedEventResponse();
        typedResponse.log = log;
        typedResponse.sourceCardId = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.destCardId = (String) eventValues.getNonIndexedValues().get(1).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
        typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
        typedResponse.txHash = (byte[]) eventValues.getNonIndexedValues().get(4).getValue();
        return typedResponse;
    }

    public Flowable<TransferValidatedEventResponse> transferValidatedEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getTransferValidatedEventFromLog(log));
    }

    public Flowable<TransferValidatedEventResponse> transferValidatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFERVALIDATED_EVENT));
        return transferValidatedEventFlowable(filter);
    }

    public RemoteFunctionCall<Tuple4<String, String, BigInteger, BigInteger>> transfers(
            byte[] param0) {
        final Function function = new Function(FUNC_TRANSFERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple4<String, String, BigInteger, BigInteger>>(function,
                new Callable<Tuple4<String, String, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple4<String, String, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<String, String, BigInteger, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> validateTransfer(String _sourceCardId,
            String _destCardId, BigInteger _amount, byte[] _txHash) {
        final Function function = new Function(
                FUNC_VALIDATETRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_sourceCardId), 
                new org.web3j.abi.datatypes.Utf8String(_destCardId), 
                new org.web3j.abi.datatypes.generated.Uint256(_amount), 
                new org.web3j.abi.datatypes.generated.Bytes32(_txHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static TransactionValidator load(String contractAddress, Web3j web3j,
            Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new TransactionValidator(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static TransactionValidator load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new TransactionValidator(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static TransactionValidator load(String contractAddress, Web3j web3j,
            Credentials credentials, ContractGasProvider contractGasProvider) {
        return new TransactionValidator(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static TransactionValidator load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new TransactionValidator(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<TransactionValidator> deploy(Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(TransactionValidator.class, web3j, credentials, contractGasProvider, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<TransactionValidator> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(TransactionValidator.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    public static RemoteCall<TransactionValidator> deploy(Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(TransactionValidator.class, web3j, transactionManager, contractGasProvider, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<TransactionValidator> deploy(Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(TransactionValidator.class, web3j, transactionManager, gasPrice, gasLimit, getDeploymentBinary(), "");
    }


    private static String getDeploymentBinary() {
        if (librariesLinkedBinary != null) {
            return librariesLinkedBinary;
        } else {
            return BINARY;
        }
    }

    public static class TransferValidatedEventResponse extends BaseEventResponse {
        public String sourceCardId;

        public String destCardId;

        public BigInteger amount;

        public BigInteger timestamp;

        public byte[] txHash;
    }
}
