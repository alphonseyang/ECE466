clc;clear all;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Reading data from a file
%Note that time is in micro seconds and packetsize is in Bytes
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


%trace file
[packet_no_p, time_p, packetsize_p, a, b, c] = textread('poisson3.data', '%f %f %f %f %f %f');

time_array = zeros(1,30000);
cumulative_arrival = zeros(1,30000);

time_array(1) = time_p(1);
cumulative_arrival(1) = packetsize_p(1);
i=2
while i<=30000
    time_array(i) = time_p(i);
    cumulative_arrival(i) = cumulative_arrival(i-1) + packetsize_p(i);
    i=i+1;
end



%token bucket output
[arrival_time, packetsize_p2, back_log, num_of_tokens] = textread('pos2_bucket.txt', '%f %f %f %f');
time_array2 = zeros(1,30000);
cumulative_arrival2 = zeros(1,30000);

time_array2(1) = arrival_time(1);
cumulative_arrival2(1) = packetsize_p2(1);
i=2
while i<=30000
    time_array2(i) = time_array2(i-1) + arrival_time(i);
    cumulative_arrival2(i) = cumulative_arrival2(i-1) + packetsize_p2(i);
    i=i+1;
end


%sink output
[packet_no_p3, packetsize_p3, arrival_time] = textread('pos2_TrafficSinkOutput.txt', '%f %f %f');
time_array3 = zeros(1,30000);
cumulative_arrival3 = zeros(1,30000);

time_array3(1) = arrival_time(1);
cumulative_arrival3(1) = packetsize_p3(1);
i=2
while i<=30000
    time_array3(i) = time_array3(i-1) + arrival_time(i);
    cumulative_arrival3(i) = cumulative_arrival3(i-1) + packetsize_p3(i);
    i=i+1;
end


%plot that shows the content of the token bucket and the backlog
%in the buffer as a function of time
figure(1);
subplot(1,1,1);
num_of_tokens_to_plot = zeros(1,30000);
back_log_to_plot = zeros(1,30000);
i=1;
while i<=30000
    num_of_tokens_to_plot(i) = num_of_tokens(i);
    back_log_to_plot(i) = back_log(i);
    i=i+1;
end
axis([1 50000 -100 18000])
h1 = plot(time_array2,num_of_tokens_to_plot, 'r',time_array2, back_log_to_plot,'b')
hold on
hkeg1= legend(h1,'number of tokens','backlog');


title('Token Bucket');
xlabel('time (in microseconds)');
ylabel('token bucket and backlog');

figure(3);
h2 = plot(time_array,cumulative_arrival, 'r', time_array2,cumulative_arrival2, 'g', time_array3,cumulative_arrival3, 'b' );
hold on
hkeg2 = legend(h2, 'trace file', 'bucket starter', 'traffic sink');
title('Poisson data');
xlabel('time (in microseconds)');
ylabel('culmulative arrival (in bytes)');