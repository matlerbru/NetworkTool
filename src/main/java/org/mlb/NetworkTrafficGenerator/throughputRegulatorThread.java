package org.mlb.NetworkTrafficGenerator;

import org.mlb.NetworkTool.Utility;

import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

public class throughputRegulatorThread extends Thread {

    public throughputRegulatorThread() {
    }

    private Double feedback;

    private double reference;

    private int samplingTime = 1000;

    private double kp;

    private double ki;

    private double output;


    ArrayList<Double> samples = new ArrayList<Double>();


    public double getFeedback() {
        return feedback;
    }

    public void setFeedback(double feedback) {
        this.feedback = feedback;
    }

    public double getReference() {
        return reference;
    }

    public void setReference(double reference) {
        this.reference = reference;
    }

    public int getSamplingTime() {
        return samplingTime;
    }

    public void setSamplingTime(int samplingTime) {
        this.samplingTime = samplingTime;
    }

    public double getKp() {
        return kp;
    }

    public void setKp(double kp) {
        this.kp = kp;
    }

    public double getKi() {
        return ki;
    }

    public void setKi(double ki) {
        this.ki = ki;
    }

    public double getOutput() {
        return output;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    @Override
    public void run(){
        try {


            NetworkLoadReader throughput = new NetworkLoadReader();

            while(true) {

                System.out.println("Regulator run");
                addSampleToSamples(10.1); //throughput here

                Utility.Threads.sleep(samplingTime);

                System.out.println(Thread.interrupted());
                if (Thread.interrupted()) {
                    System.out.println("Regulator interrupted");
                    throw new InterruptedException("Interrupted");
                }

            }
        } catch (InterruptedException e) {
            System.out.println("Regulator thread ended");
        }
    }

    private void addSampleToSamples(Double sample) {
        samples.add(0, sample);
        samples.remove(samples.remove(samples.size() - 1));
    }
















}
