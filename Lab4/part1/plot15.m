[seqNo, sent, received] = textread('sinkOutput.txt', '%d %d %d');
figure(1);
plot(seqNo,sent,seqNo,received);
title('Packet train: N=100, L=400, r=10');
xlabel('Sequence Numbers');
ylabel('Timestamps (in microseconds)');
legend('Sent packets','Received packets');